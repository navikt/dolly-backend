package no.nav.dolly.bestilling.arenaforvalter;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.credentials.ArenaforvalterProxyProperties;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaArbeidssokerBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaDagpenger;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeDagpengerResponse;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.service.TokenService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;

@Component
@Slf4j
public class ArenaForvalterConsumer {

    private static final String ARENAFORVALTER_BRUKER = "/api/v1/bruker";
    private static final String ARENAFORVALTER_DAGPENGER = "/api/v1/dagpenger";
    private static final String ARENAFORVALTER_ENVIRONMENTS = "/api/v1/miljoe";

    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;
    private final TokenService tokenService;

    public ArenaForvalterConsumer(ArenaforvalterProxyProperties serverProperties, TokenService tokenService) {
        this.serviceProperties = serverProperties;
        this.tokenService = tokenService;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "arena_getIdent" })
    public ResponseEntity<ArenaArbeidssokerBruker> getIdent(String ident) {

        log.info("Henter bruker på ident: {} fra arena-forvalteren", ident);
        ResponseEntity<ArenaArbeidssokerBruker> response = webClient.get().uri(
                        uriBuilder -> uriBuilder
                                .path(ARENAFORVALTER_BRUKER)
                                .queryParam("filter-personident", ident)
                                .build())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                .retrieve().toEntity(ArenaArbeidssokerBruker.class).block();

        if (nonNull(response) && response.hasBody()) {
            log.info("Hentet bruker fra arena: {}", Json.pretty(response.getBody()));
        }
        return response;
    }

    @Timed(name = "providers", tags = { "operation", "arena_deleteIdent" })
    public ResponseEntity<JsonNode> deleteIdent(String ident, String environment) {

        return webClient.delete().uri(
                        uriBuilder -> uriBuilder
                                .path(ARENAFORVALTER_BRUKER)
                                .queryParam("miljoe", environment)
                                .queryParam("personident", ident)
                                .build())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "arena_postBruker" })
    public ResponseEntity<ArenaNyeBrukereResponse> postArenadata(ArenaNyeBrukere arenaNyeBrukere) {

        return webClient.post().uri(
                        uriBuilder -> uriBuilder
                                .path(ARENAFORVALTER_BRUKER)
                                .build())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                .bodyValue(arenaNyeBrukere)
                .retrieve()
                .toEntity(ArenaNyeBrukereResponse.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "arena_postDagpenger" })
    public ResponseEntity<ArenaNyeDagpengerResponse> postArenaDagpenger(ArenaDagpenger arenaDagpenger) {

        return webClient.post().uri(
                        uriBuilder -> uriBuilder
                                .path(ARENAFORVALTER_DAGPENGER)
                                .build())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                .bodyValue(arenaDagpenger)
                .retrieve()
                .toEntity(ArenaNyeDagpengerResponse.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "arena_getEnvironments" })
    public List<String> getEnvironments() {

        return webClient.get().uri(
                        uriBuilder -> uriBuilder
                                .path(ARENAFORVALTER_ENVIRONMENTS)
                                .build())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
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
}
