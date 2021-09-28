package no.nav.dolly.bestilling.krrstub;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.credentials.KrrstubProxyProperties;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.service.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.AccessControlException;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_PERSON_IDENT;

@Slf4j
@Service
public class KrrstubConsumer {

    private static final String DIGITAL_KONTAKT_URL = "/api/v1/kontaktinformasjon";
    private static final String PERSON_DIGITAL_KONTAKT_URL = "/api/v1/person/kontaktinformasjon";

    private final WebClient webClient;
    private final TokenService tokenService;
    private final NaisServerProperties serverProperties;

    public KrrstubConsumer(TokenService tokenService, KrrstubProxyProperties serverProperties) {
        this.tokenService = tokenService;
        this.serverProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "krrstub_getKontaktdata" })
    public ResponseEntity<List<DigitalKontaktdata>> getDigitalKontaktdata(String ident) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PERSON_DIGITAL_KONTAKT_URL)
                        .build())
                .header(HEADER_NAV_CALL_ID, getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .retrieve().toEntityList(DigitalKontaktdata.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "krrstub_createKontaktdata" })
    public ResponseEntity<Object> createDigitalKontaktdata(DigitalKontaktdata digitalKontaktdata) {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(DIGITAL_KONTAKT_URL)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HEADER_NAV_CALL_ID, getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .bodyValue(digitalKontaktdata)
                .retrieve().toEntity(Object.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "krrstub_deleteKontaktdata" })
    public ResponseEntity<Object> deleteDigitalKontaktdata(Long id) {

        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(DIGITAL_KONTAKT_URL)
                        .pathSegment(id.toString())
                        .build())
                .header(HEADER_NAV_CALL_ID, getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .retrieve().toEntity(Object.class)
                .block();
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }

    private String getAccessToken() {
        AccessToken token = tokenService.generateToken(serverProperties).block();
        if (isNull(token)) {
            throw new AccessControlException("Klarte ikke å generere AccessToken for dokarkiv-proxy");
        }
        return token.getTokenValue();
    }

}