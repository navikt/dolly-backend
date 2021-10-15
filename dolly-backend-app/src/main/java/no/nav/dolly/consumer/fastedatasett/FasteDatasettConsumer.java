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

import java.security.AccessControlException;

import static java.util.Objects.isNull;

@Component
public class FasteDatasettConsumer {

    private static final String REQUEST_URL = "/api/v1/faste-data";
    private static final String GRUPPE_REQUEST_URL = REQUEST_URL + "/tps";
    private static final String EREG_REQUEST_URL = REQUEST_URL + "/ereg";
    private static final String GRUPPE_QUERY = "gruppe";

    private final TokenService tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serverProperties;

    public FasteDatasettConsumer(TokenService tokenService, StatiskDataForvalterProxyProperties serverProperties) {
        this.tokenService = tokenService;
        this.serverProperties = serverProperties;
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
        AccessToken token = tokenService.generateToken(serverProperties).block();
        if (isNull(token)) {
            throw new AccessControlException("Klarte ikke å generere AccessToken for FasteDataSett-Proxy");
        }
        return "Bearer " + token.getTokenValue();
    }
}
