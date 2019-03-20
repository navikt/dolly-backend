package no.nav.dolly.aareg;

import static java.util.Arrays.asList;
import static no.nav.dolly.config.CachingConfig.CACHE_FASIT;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import no.nav.dolly.fasit.FasitApiConsumer;
import no.nav.dolly.fasit.FasitResourceWithUnmappedProperties;

@Service
public class AaregArbeidsforholdFasitConsumer {

    private static final String BASE_URL = "BaseUrl";
    private static final String BEHANDLE_ARBEIDFORHOLD_ALIAS = "virksomhet:BehandleArbeidsforhold_v1";
    private static final String BEHANDLE_ARBEIDSFORHOLD_SERVICE_URL = "/aareg-core/BehandleArbeidsforholdService/v1";

    private static final String REST_SERVICE = "RestService";
    private static final String AAREG_REST_ALIAS = "aareg.api";
    private static final String ARBEIDSFORHOLD_SERVICE_URL = "/v1/arbeidstaker/arbeidsforhold";

    private static final String FAGSYSTEM = "fss";

    @Autowired
    private FasitApiConsumer fasitApiConsumer;

    public Map<String, String> fetchWsUrlsAllEnvironments() {

        FasitResourceWithUnmappedProperties[] fasitResources = fasitApiConsumer.fetchResources(BEHANDLE_ARBEIDFORHOLD_ALIAS, BASE_URL);

        return asList(fasitResources).stream()
                .filter(resource -> FAGSYSTEM.equals(resource.getScope().getZone()))
                .collect(Collectors.toMap(
                        resource -> resource.getScope().getEnvironment(),
                        resource -> ((String) ((Map) resource.getProperties()).get("url"))
                                .contains("/aareg-services/BehandleArbeidsforholdService/v1") ?
                                ((String) ((Map) resource.getProperties()).get("url")) :
                                ((Map) resource.getProperties()).get("url") + BEHANDLE_ARBEIDSFORHOLD_SERVICE_URL));
    }

    @Cacheable(CACHE_FASIT)
    public Map<String, String> fetchRestUrlsAllEnvironments() {

        FasitResourceWithUnmappedProperties[] fasitResources = fasitApiConsumer.fetchResources(AAREG_REST_ALIAS, REST_SERVICE);

        return asList(fasitResources).stream()
                .filter(resource -> FAGSYSTEM.equals(resource.getScope().getZone()))
                .collect(Collectors.toMap(
                        resource -> resource.getScope().getEnvironment(),
                        resource -> ((Map) resource.getProperties()).get("url") + ARBEIDSFORHOLD_SERVICE_URL));
    }
}
