package no.nav.dolly.bestilling.skjermingsregister;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataRequest;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataResponse;
import no.nav.dolly.config.credentials.SkjermingsregisterProxyProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
public class SkjermingsRegisterConsumer {

    private static final String SKJERMINGSREGISTER_URL = "/api/v1/skjermingdata";
    private static final String SKJERMINGOPPHOER_URL = SKJERMINGSREGISTER_URL + "/opphor";

    private final TokenService tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public SkjermingsRegisterConsumer(TokenService tokenService, SkjermingsregisterProxyProperties serverProperties) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "skjermingsdata-opprett" })
    public ResponseEntity<List<SkjermingsDataResponse>> postSkjerming(List<SkjermingsDataRequest> skjermingsDataRequest) {

        String callid = getNavCallId();
        logInfoSkjermingsMelding(callid);

        return webClient.post().uri(uriBuilder -> uriBuilder
                        .path(SKJERMINGSREGISTER_URL)
                        .build())
                .header(AUTHORIZATION, getAccessToken())
                .header(HEADER_NAV_CALL_ID, callid)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(skjermingsDataRequest)
                .retrieve().toEntityList(SkjermingsDataResponse.class)
                .block();

    }

    @Timed(name = "providers", tags = { "operation", "skjermingsdata-hent" })
    public ResponseEntity<SkjermingsDataResponse> getSkjerming(String ident) {

        String callid = getNavCallId();
        logInfoSkjermingsMelding(callid);

        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path(SKJERMINGSREGISTER_URL)
                        .pathSegment(ident)
                        .build())
                .header(AUTHORIZATION, getAccessToken())
                .header(HEADER_NAV_CALL_ID, callid)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve().toEntity(SkjermingsDataResponse.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "skjermingsdata-opphoer" })
    public ResponseEntity<String> putSkjerming(String ident) {

        String callid = getNavCallId();
        logInfoSkjermingsMelding(callid);

        return webClient.put().uri(uriBuilder -> uriBuilder
                        .path(SKJERMINGOPPHOER_URL)
                        .pathSegment(ident)
                        .build())
                .header(AUTHORIZATION, getAccessToken())
                .header(HEADER_NAV_CALL_ID, callid)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve().toEntity(String.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "skjermingsdata-slett" })
    public void deleteSkjerming(String fnr) {

        String callid = getNavCallId();
        logInfoSkjermingsMelding(callid);

        webClient.delete().uri(uriBuilder -> uriBuilder
                        .path(SKJERMINGSREGISTER_URL)
                        .pathSegment(fnr)
                        .build())
                .header(AUTHORIZATION, getAccessToken())
                .header(HEADER_NAV_CALL_ID, callid)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve().toEntity(String.class)
                .block();
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }

    private void logInfoSkjermingsMelding(String callId) {

        log.info("Skjermingsmelding sendt, callid: {}, consumerId: {}", callId, CONSUMER);
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
