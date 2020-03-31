package no.nav.dolly.bestilling.udistub;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;

import java.net.URI;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.udistub.model.UdiPerson;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;

@Slf4j
@Service
@RequiredArgsConstructor
public class UdiStubConsumer {

    private static final String CONSUMER = "Dolly";
    private static final String NAV_PERSON_IDENT = "Nav-Personident";
    private static final String NAV_CALL_ID = "Nav-Call-Id";
    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";
    private static final String UDI_STUB_PERSON = "/api/v1/person";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    @Timed(name = "providers", tags = { "operation", "udi_createPerson" })
    public ResponseEntity<UdiPersonControllerResponse> createUdiPerson(UdiPerson udiPerson) {

            return restTemplate.exchange(RequestEntity.post(URI.create(providersProps.getUdiStub().getUrl() + UDI_STUB_PERSON))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HEADER_NAV_CALL_ID, getNavCallId())
                            .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                            .body(udiPerson),
                    UdiPersonControllerResponse.class);
    }

    @Timed(name = "providers", tags = { "operation", "udi_deletePerson" })
    public ResponseEntity<Object> deleteUdiPerson(String ident) {

            return restTemplate.exchange(RequestEntity.delete(URI.create(providersProps.getUdiStub().getUrl() + UDI_STUB_PERSON))
                            .header(HEADER_NAV_CALL_ID, getNavCallId())
                            .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                            .header(NAV_PERSON_IDENT, ident)
                            .build(), Object.class);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}