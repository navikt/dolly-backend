package no.nav.dolly.bestilling.aareg.amelding;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.AMeldingDTO;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;

@Service
@RequiredArgsConstructor
public class AmeldingConsumer {

    private static final String AMELDING_URL = "/api/v1/amelding";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    @Timed(name = "providers", tags = { "operation", "amelding_get" })
    public ResponseEntity<AMeldingDTO> getAmelding(String id) {
        return restTemplate.exchange(RequestEntity.get(
                URI.create(format("%s%s/%s", providersProps.getAmeldingService().getUrl(), AMELDING_URL, id)))
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .build(), AMeldingDTO.class);
    }

    @Timed(name = "providers", tags = { "operation", "amelding_put" })
    public ResponseEntity<?> putAmeldingdata(AMeldingDTO amelding) {
        return restTemplate.exchange(RequestEntity.put(
                URI.create(providersProps.getAmeldingService().getUrl() + AMELDING_URL))
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .body(amelding), ArenaNyeBrukereResponse.class);
    }
}
