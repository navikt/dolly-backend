package no.nav.dolly.consumer.kodeverk;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.consumer.kodeverk.domain.GetKodeverkKoderBetydningerResponse;
import no.nav.dolly.exceptions.KodeverkException;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;

@Component
@RequiredArgsConstructor
public class KodeverkConsumer {

    private static final String QUERY_PARAMS = "?ekskluderUgyldige=true&spraak=nb";
    private static final String KODEVERK_URL_BASE = "/api/v1/kodeverk/{kodeverksnavn}/koder/betydninger";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    @Timed(name = "providers", tags = { "operation", "hentKodeverk" })
    public GetKodeverkKoderBetydningerResponse fetchKodeverkByName(String kodeverk) {

        return getKodeverk(kodeverk);
    }

    @Timed(name = "providers", tags = { "operation", "hentKodeverk" })
    public Map<String, String> getKodeverkByName(String kodeverk) {

        return getKodeverk(kodeverk).getBetydninger().entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().get(0).getBeskrivelser().get("nb").getTekst()));
    }

    private GetKodeverkKoderBetydningerResponse getKodeverk(String kodeverk) {

        try {
            return restTemplate.exchange(RequestEntity.get(
                    URI.create(providersProps.getKodeverk().getUrl() + getKodeverksnavnUrl(kodeverk) + QUERY_PARAMS))
                    .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                    .header(HEADER_NAV_CALL_ID, generateCallId())
                    .build(), GetKodeverkKoderBetydningerResponse.class).getBody();

        } catch (HttpClientErrorException e) {
            throw new KodeverkException(e.getStatusCode(), e.getResponseBodyAsString());
        }
    }

    private String getKodeverksnavnUrl(String kodeverksnavn) {
        return KODEVERK_URL_BASE.replace("{kodeverksnavn}", kodeverksnavn);
    }
}
