package no.nav.dolly.bestilling.organisasjonforvalter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonResponse;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.AccessScopes;
import no.nav.dolly.security.AccessToken;
import no.nav.dolly.security.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisasjonConsumer {

    private static final String ORGANISASJON_FORVALTER_URL = "/api/organisasjon/";

    private final WebClient webClient;
    private final ProvidersProps providersProps;
    private final TokenService tokenService;

    @Value("${ORGANISASJON_FORVALTER_CLIENT_ID}")
    private String organisasjonerClientId;

    @Timed(name = "providers", tags = { "operation", "organisasjon-opprett" })
    public ResponseEntity<OrganisasjonResponse> postOrganisasjon(OrganisasjonRequest organisasjonRequest) {

        String callId = getNavCallId();
        log.info("Organisasjon oppretting sendt, callId: {}, consumerId: {}", callId, CONSUMER);

        AccessToken accessToken = tokenService.getAccessToken(
                new AccessScopes("api://" + organisasjonerClientId + "/.default")
        );

        return webClient
                .post()
                .uri(URI.create(providersProps.getOrganisasjonForvalter().getUrl() + ORGANISASJON_FORVALTER_URL))
                .header(AUTHORIZATION, "Bearer " + accessToken.getTokenValue())
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .body(organisasjonRequest, OrganisasjonRequest.class)
                .retrieve()
                .toEntity(OrganisasjonResponse.class)
                .block();
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}
