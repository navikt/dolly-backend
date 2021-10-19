package no.nav.dolly.consumer.fastedatasett;

import com.fasterxml.jackson.databind.JsonNode;
import no.nav.dolly.config.credentials.StatiskDataForvalterProxyProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.service.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

import static java.util.Objects.isNull;

@Component
public class FasteDatasettConsumer {

    private static final String REQUEST_URL = "/api/v1/faste-data";
    private static final String GRUPPE_REQUEST_URL = REQUEST_URL + "/tps";
    private static final String EREG_REQUEST_URL = REQUEST_URL + "/ereg";
    private static final String GRUPPE_QUERY = "gruppe";

    private final TokenService tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public FasteDatasettConsumer(TokenService tokenService, StatiskDataForvalterProxyProperties serverProperties) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl()).build();
    }

    @Timed(name = "providers", tags = { "operation", "hentFasteDatasett" })
    public ResponseEntity<JsonNode> hentDatasett(DatasettType datasettType) {

        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path(REQUEST_URL)
                        .pathSegment(datasettType.getUrl())
                        .build())
                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                .retrieve().toEntity(JsonNode.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "hentOrgnummer" })
    public ResponseEntity<JsonNode> hentOrgnummer() {

        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path(EREG_REQUEST_URL)
                        .queryParam(GRUPPE_QUERY, "DOLLY")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                .retrieve().toEntity(JsonNode.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "hentFasteDatasettGruppe" })
    public ResponseEntity<JsonNode> hentDatasettGruppe(String gruppe) {

        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path(GRUPPE_REQUEST_URL)
                        .queryParam(GRUPPE_QUERY, gruppe)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                .retrieve().toEntity(JsonNode.class)
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
