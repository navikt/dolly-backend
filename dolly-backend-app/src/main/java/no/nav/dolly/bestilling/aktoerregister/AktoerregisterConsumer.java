package no.nav.dolly.bestilling.aktoerregister;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.StsOidcService;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_PERSON_IDENTER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@Slf4j
@RequiredArgsConstructor
public class AktoerregisterConsumer {

    private static final String AKTOER_URL = "/api/v1/identer";
    private static final String PREPROD_ENV = "q";

    private final ProvidersProps providersProps;
    private final RestTemplate restTemplate;
    private final StsOidcService stsOidcService;

    @Timed(name = "providers", tags = { "operation", "aktoerregister_getId" })
    public Map<String, Map> getAktoerId(String ident) {

        ResponseEntity<Map> response = restTemplate.exchange(RequestEntity.get(
                        URI.create(providersProps.getAktoerregister().getUrl() + AKTOER_URL))
                .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_CALL_ID, getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HEADER_NAV_PERSON_IDENTER, ident)
                .build(), Map.class);
        if (response.hasBody()) {
            log.info("Response fra aktoerreister: {}", response.getBody());
        }
        return response.hasBody() ? response.getBody() : emptyMap();
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}
