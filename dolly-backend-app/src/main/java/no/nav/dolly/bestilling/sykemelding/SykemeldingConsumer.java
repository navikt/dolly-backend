package no.nav.dolly.bestilling.sykemelding;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.domain.SyntSykemeldingRequest;
import no.nav.dolly.config.credentials.SykemeldingApiProxyProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.service.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

@Slf4j
@Service
public class SykemeldingConsumer {

    public static final String SYNT_SYKEMELDING_URL = "/synt-sykemelding/api/v1/synt-sykemelding";
    public static final String DETALJERT_SYKEMELDING_URL = "/sykemelding/api/v1/sykemeldinger";

    private final WebClient webClient;
    private final TokenService tokenService;
    private final NaisServerProperties serviceProperties;

    public SykemeldingConsumer(
            TokenService accessTokenService,
            SykemeldingApiProxyProperties serverProperties
    ) {
        this.tokenService = accessTokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl()).build();
    }

    @Timed(name = "providers", tags = { "operation", "syntsykemelding_opprett" })
    public ResponseEntity<String> postSyntSykemelding(SyntSykemeldingRequest sykemeldingRequest) {

        String callId = getNavCallId();
        log.info("Synt Sykemelding sendt, callId: {}, consumerId: {}", callId, CONSUMER);

        return webClient.post().uri(uriBuilder -> uriBuilder
                        .path(SYNT_SYKEMELDING_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                .bodyValue(sykemeldingRequest)
                .retrieve().toEntity(String.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "detaljertsykemelding_opprett" })
    public ResponseEntity<String> postDetaljertSykemelding(DetaljertSykemeldingRequest detaljertSykemeldingRequest) {

        String callId = getNavCallId();
        log.info("Detaljert Sykemelding sendt, callId: {}, consumerId: {}", callId, CONSUMER);

        return webClient.post().uri(uriBuilder -> uriBuilder
                        .path(DETALJERT_SYKEMELDING_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                .bodyValue(detaljertSykemeldingRequest)
                .retrieve().toEntity(String.class)
                .block();
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

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}
