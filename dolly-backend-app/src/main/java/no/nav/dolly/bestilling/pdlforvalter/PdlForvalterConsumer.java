package no.nav.dolly.bestilling.pdlforvalter;

import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_TOKEN;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_PERSON_IDENT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdressebeskyttelse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlDoedsfall;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFamilierelasjon;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFoedsel;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKjoenn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlNavn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpprettPerson;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlStatsborgerskap;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.PdlFalskIdentitet;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.StsOidcService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlForvalterConsumer {

    private static final String PDL_BESTILLING_URL = "/api/v1/bestilling";
    private static final String PDL_BESTILL_KONTAKTINFORMASJON_FOR_DODESDBO_URL = PDL_BESTILLING_URL + "/kontaktinformasjonfordoedsbo";
    private static final String PDL_BESTILLING_UTENLANDS_IDENTIFIKASJON_NUMMER_URL = PDL_BESTILLING_URL + "/utenlandsidentifikasjonsnummer";
    private static final String PDL_BESTILLING_FALSK_IDENTITET_URL = PDL_BESTILLING_URL + "/falskidentitet";
    private static final String PDL_BESTILLING_OPPRETT_PERSON = PDL_BESTILLING_URL + "/opprettperson";
    private static final String PDL_BESTILLING_FOEDSEL_URL = PDL_BESTILLING_URL + "/foedsel";
    private static final String PDL_BESTILLING_FAMILIERELASJON = PDL_BESTILLING_URL + "/familierelasjon";
    private static final String PDL_BESTILLING_DOEDSFALL_URL = PDL_BESTILLING_URL + "/doedsfall";
    private static final String PDL_BESTILLING_ADRESSEBESKYTTELSE_URL = PDL_BESTILLING_URL + "/adressebeskyttelse";
    private static final String PDL_BESTILLING_NAVN_URL = PDL_BESTILLING_URL + "/navn";
    private static final String PDL_BESTILLING_KJOENN_URL = PDL_BESTILLING_URL + "/kjoenn";
    private static final String PDL_BESTILLING_STATSBORGERSKAP_URL = PDL_BESTILLING_URL + "/statsborgerskap";
    private static final String PDL_BESTILLING_SLETTING_URL = "/api/v1/ident";
    private static final String PDL_PERSONSTATUS = "/api/v1/personstatus";
    private static final String PREPROD_ENV = "q";

    private static final String SEND_ERROR = "Feilet å sende %s for ident %s til PDL-forvalter";
    private static final String SEND_ERROR_2 = SEND_ERROR + ": %s";

    @Value("${dolly.environment.name}")
    private String environment;

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;
    private final StsOidcService stsOidcService;

    public ResponseEntity deleteIdent(String ident) {
        return restTemplate.exchange(RequestEntity.delete(
                URI.create(providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_SLETTING_URL))
                .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_CONSUMER_TOKEN, resolveToken())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .build(), JsonNode.class);
    }

    public ResponseEntity<JsonNode> getPersonstatus(String ident) {
        return restTemplate.exchange(RequestEntity.get(
                URI.create(providersProps.getPdlForvalter().getUrl() + PDL_PERSONSTATUS))
                .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_CONSUMER_TOKEN, resolveToken())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .build(), JsonNode.class);
    }

    public ResponseEntity postOpprettPerson(PdlOpprettPerson pdlNavn, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_OPPRETT_PERSON,
                pdlNavn, ident);
    }

    public ResponseEntity postNavn(PdlNavn pdlNavn, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_NAVN_URL,
                pdlNavn, ident);
    }

    public ResponseEntity postKjoenn(PdlKjoenn pdlNavn, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_KJOENN_URL,
                pdlNavn, ident);
    }

    public ResponseEntity postKontaktinformasjonForDoedsbo(PdlKontaktinformasjonForDoedsbo kontaktinformasjonForDoedsbo, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILL_KONTAKTINFORMASJON_FOR_DODESDBO_URL,
                kontaktinformasjonForDoedsbo, ident);
    }

    public ResponseEntity postUtenlandskIdentifikasjonsnummer(PdlUtenlandskIdentifikasjonsnummer utenlandskIdentifikasjonsnummer, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_UTENLANDS_IDENTIFIKASJON_NUMMER_URL,
                utenlandskIdentifikasjonsnummer, ident);
    }

    public ResponseEntity postFalskIdentitet(PdlFalskIdentitet falskIdentitet, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_FALSK_IDENTITET_URL, falskIdentitet, ident);
    }

    public ResponseEntity postStatsborgerskap(PdlStatsborgerskap pdlStatsborgerskap, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_STATSBORGERSKAP_URL, pdlStatsborgerskap, ident);
    }

    public ResponseEntity postDoedsfall(PdlDoedsfall pdlDoedsfall, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_DOEDSFALL_URL,
                pdlDoedsfall, ident);
    }

    public ResponseEntity postFoedsel(PdlFoedsel pdlFoedsel, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_FOEDSEL_URL, pdlFoedsel, ident);
    }

    public ResponseEntity postAdressebeskyttelse(PdlAdressebeskyttelse pdlAdressebeskyttelse, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_ADRESSEBESKYTTELSE_URL,
                pdlAdressebeskyttelse, ident);
    }

    public ResponseEntity postFamilierelasjon(PdlFamilierelasjon familierelasjonn, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_FAMILIERELASJON,
                familierelasjonn, ident);
    }

    private ResponseEntity postRequest(String url, Object body, String ident) {

        return restTemplate.exchange(RequestEntity.post(URI.create(url))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_CONSUMER_TOKEN, resolveToken())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .body(body), JsonNode.class);
    }

    private String resolveToken() {

        return environment.toLowerCase().contains(PREPROD_ENV) ? StsOidcService.getUserIdToken() : stsOidcService.getIdToken(PREPROD_ENV);
    }
}
