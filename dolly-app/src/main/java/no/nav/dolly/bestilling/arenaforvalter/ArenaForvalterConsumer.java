package no.nav.dolly.bestilling.arenaforvalter;

import static java.lang.String.format;

import java.net.URI;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukereMedServicebehov;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaServicedata;
import no.nav.dolly.properties.ProvidersProps;

@Service
public class ArenaForvalterConsumer {

    private static final String ARENABRUKER_MED_SERVICEBEHOV_URL = "/api/v1/bruker";
    private static final String ARENABRUKER_UTEN_SERVICEBEHOV_URL = "/api/v1/brukerUtenServiceBehov";
    private static final String NAV_CALL_ID = "Nav-Call-Id";
    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";
    private static final String KILDE = "Dolly";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProvidersProps providersProps;

    public ResponseEntity getIdent(String ident) {
        return restTemplate.exchange(RequestEntity.get(
                URI.create(format("%s%s?filter-personident=%s", providersProps.getArenaForvalter().getUrl(), ARENABRUKER_MED_SERVICEBEHOV_URL, ident)))
                .header(NAV_CALL_ID, getCallId())
                .header(NAV_CONSUMER_ID, KILDE)
                .build(), JsonNode.class);
    }

    public ResponseEntity deleteIdent(String ident) {
        return restTemplate.exchange(RequestEntity.delete(
                URI.create(format("%s%s?personident=%s", providersProps.getArenaForvalter().getUrl(), ARENABRUKER_MED_SERVICEBEHOV_URL, ident)))
                .header(NAV_CALL_ID, getCallId())
                .header(NAV_CONSUMER_ID, KILDE)
                .build(), JsonNode.class);
    }

    public ResponseEntity postArenadata(ArenaServicedata arenaServicedata) {
        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getArenaForvalter().getUrl() +
                        (arenaServicedata instanceof ArenaBrukereMedServicebehov ? ARENABRUKER_MED_SERVICEBEHOV_URL : ARENABRUKER_UTEN_SERVICEBEHOV_URL)))
                .header(NAV_CALL_ID, getCallId())
                .header(NAV_CONSUMER_ID, KILDE)
                .body(arenaServicedata), JsonNode.class);
    }


    private static String getCallId() {
        return "Dolly: " + UUID.randomUUID().toString();
    }
}