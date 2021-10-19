package no.nav.dolly.bestilling.sigrunstub;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunResponse;
import no.nav.dolly.config.credentials.SigrunstubProxyProperties;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
public class SigrunStubConsumer {

    private static final String CONSUMER = "Dolly";
    private static final String URL_VERSION = "/api/v1";
    private static final String SIGRUN_STUB_DELETE_GRUNNLAG = URL_VERSION + "/slett";
    private static final String SIGRUN_STUB_OPPRETT_GRUNNLAG = URL_VERSION + "/lignetinntekt";

    private final TokenService tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public SigrunStubConsumer(TokenService tokenService, SigrunstubProxyProperties serverProperties) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "sigrun_deleteGrunnlag" })
    public void deleteSkattegrunnlag(String ident) {

        var callId = getNavCallId();
        log.info("Delete til sigrun-stub med call-id {}", callId);

        ResponseEntity<String> response = webClient.delete().uri(uriBuilder -> uriBuilder
                        .path(SIGRUN_STUB_DELETE_GRUNNLAG)
                        .build())
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(AUTHORIZATION, getAccessToken())
                .header("personidentifikator", ident)
                .retrieve().toEntity(String.class)
                .block();

        if (isNull(response) || !response.getStatusCode().is2xxSuccessful()) {
            throw new DollyFunctionalException(String.format("Klarte ikke å slette skattegrunnlag for ident %s i sigrunstub", ident));
        }
    }

    @Timed(name = "providers", tags = { "operation", "sigrun_createGrunnlag" })
    public ResponseEntity<SigrunResponse> createSkattegrunnlag(List<OpprettSkattegrunnlag> request) {

        var callId = getNavCallId();
        log.info("Post til sigrun-stub med call-id {} og data {}", callId, request);

        return webClient.post().uri(uriBuilder -> uriBuilder
                        .path(SIGRUN_STUB_OPPRETT_GRUNNLAG)
                        .build())
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(AUTHORIZATION, getAccessToken())
                .bodyValue(request)
                .retrieve().toEntity(SigrunResponse.class)
                .block();
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }

    private String getAccessToken() {
        AccessToken token = tokenService.generateToken(serviceProperties).block();
        if (isNull(token)) {
            throw new SecurityException(String.format("Klarte ikke å generere AccessToken for %s", serviceProperties.getName()));
        }
        return "Bearer " + token.getTokenValue();
    }

    public Map<String, String> checkAlive() {
        try {
            return Map.of(serviceProperties.getName(), serviceProperties.checkIsAlive(webClient, getAccessToken()));
        } catch (SecurityException | WebClientResponseException ex) {
            return Map.of(serviceProperties.getName(), String.format("%s, URL: %s", ex.getMessage(), serviceProperties.getUrl()));
        }
    }
}