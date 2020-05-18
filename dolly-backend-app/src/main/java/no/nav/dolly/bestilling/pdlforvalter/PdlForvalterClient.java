package no.nav.dolly.bestilling.pdlforvalter;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.reverse;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;
import static no.nav.dolly.domain.CommonKeys.SYNTH_ENV;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

import java.util.Iterator;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdressebeskyttelse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlDoedsfall;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFamilierelasjon;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFoedsel;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKjoenn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlNavn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpprettPerson;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlStatsborgerskap;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlTelefonnummer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.pdlforvalter.Pdldata;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.Relasjon;
import no.nav.dolly.domain.resultset.tpsf.RsBarnRequest;
import no.nav.dolly.domain.resultset.tpsf.RsPartnerRequest;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.service.TpsfPersonCache;

@Slf4j
@Order(1)
@Service
@RequiredArgsConstructor
public class PdlForvalterClient implements ClientRegister {

    public enum StausResponse {DONE, PENDING, DELETING}

    public static final String KONTAKTINFORMASJON_DOEDSBO = "KontaktinformasjonForDoedsbo";
    public static final String UTENLANDS_IDENTIFIKASJONSNUMMER = "UtenlandskIdentifikasjonsnummer";
    public static final String FALSK_IDENTITET = "FalskIdentitet";
    public static final String PDL_FORVALTER = "PdlForvalter";

    private static final String UKJENT = "U";
    private static final String HENDELSE_ID = "hendelseId";

    private final PdlForvalterConsumer pdlForvalterConsumer;
    private final TpsfPersonCache tpsfPersonCache;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (bestilling.getEnvironments().contains(SYNTH_ENV) || nonNull(bestilling.getPdlforvalter())) {

            StringBuilder status = new StringBuilder();

            if (bestilling.getEnvironments().contains(SYNTH_ENV)) {

                hentTpsPersondetaljer(tpsPerson, bestilling.getTpsf(), isOpprettEndre);
                if (!isOpprettEndre) {
                    sendDeleteIdent(tpsPerson);
                }
                sendPdlPersondetaljer(tpsPerson, status, isOpprettEndre);

                if (nonNull(bestilling.getPdlforvalter())) {
                    Pdldata pdldata = mapperFacade.map(bestilling.getPdlforvalter(), Pdldata.class);
                    sendUtenlandsid(pdldata, tpsPerson.getHovedperson(), status);
                    sendDoedsbo(pdldata, tpsPerson.getHovedperson(), status);
                    sendFalskIdentitet(pdldata, tpsPerson.getHovedperson(), status);
                }

            } else {

                status.append('$')
                        .append(PDL_FORVALTER)
                        .append("&Feil: Bestilling ble ikke sendt til Persondataløsningen (PDL) da miljø '")
                        .append(SYNTH_ENV)
                        .append("' ikke er valgt");
            }

            if (status.length() > 1) {
                progress.setPdlforvalterStatus(status.substring(1));
            }
        }
    }

    @Override
    public void release(List<String> identer) {

        try {
            identer.forEach(pdlForvalterConsumer::deleteIdent);

        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void hentTpsPersondetaljer(TpsPerson tpsPerson, RsTpsfUtvidetBestilling tpsfUtvidetBestilling, boolean isOpprettEndre) {

        tpsfPersonCache.fetchIfEmpty(tpsPerson);

        if (nonNull(tpsPerson.getPerson(tpsPerson.getHovedperson()))) {
            Person hovedperson = tpsPerson.getPerson(tpsPerson.getHovedperson());
            if (nonNull(tpsfUtvidetBestilling)) {
                if (UKJENT.equals(tpsfUtvidetBestilling.getKjonn())) {
                    hovedperson.setKjonn(UKJENT);
                }
                if (nonNull(tpsfUtvidetBestilling.getRelasjoner())) {
                    List partnereRequest = newArrayList(tpsfUtvidetBestilling.getRelasjoner().getPartnere());
                    Iterator<RsPartnerRequest> partnere = reverse(partnereRequest).iterator();
                    Iterator<RsBarnRequest> barn = tpsfUtvidetBestilling.getRelasjoner().getBarn().iterator();
                    hovedperson.getRelasjoner().forEach(relasjon -> {
                        if ((!isOpprettEndre ||
                                tpsPerson.getNyePartnereOgBarn().contains(relasjon.getPersonRelasjonMed().getIdent())) &&
                                (isKjonnUkjent(relasjon, partnere, barn) &&
                                        nonNull(tpsPerson.getPerson(relasjon.getPersonRelasjonMed().getIdent())))) {
                            tpsPerson.getPerson(relasjon.getPersonRelasjonMed().getIdent()).setKjonn(UKJENT);
                        }
                    });
                }
            }
        }
        tpsPerson.getPersondetaljer().forEach(person ->
                person.getRelasjoner().forEach(relasjon ->
                        relasjon.setPersonRelasjonTil(tpsPerson.getPerson(relasjon.getPersonRelasjonMed().getIdent()))
                )
        );
    }

    private static boolean isKjonnUkjent(Relasjon relasjon, Iterator<RsPartnerRequest> partnere,
            Iterator<RsBarnRequest> barn) {

        return relasjon.isPartner() && partnere.next().isKjonnUkjent() ||
                relasjon.isBarn() && barn.next().isKjonnUkjent();
    }

    private void sendPdlPersondetaljer(TpsPerson tpsPerson, StringBuilder status, boolean isOpprettEndre) {

        status.append('$').append(PDL_FORVALTER);

        try {
            tpsPerson.getPersondetaljer().forEach(person -> {
                sendOpprettPerson(person);
                sendFoedselsmelding(person);
                sendNavn(person);
                sendKjoenn(person, isOpprettEndre, tpsPerson.getNyePartnereOgBarn());
                sendAdressebeskyttelse(person);
                sendOppholdsadresse(person);
                sendKontaktadresse(person);
                sendStatsborgerskap(person);
                sendFamilierelasjoner(person);
                sendSivilstand(person);
                sendTelefonnummer(person);
                sendDoedsfall(person);
            });
            status.append("&OK");

        } catch (DollyFunctionalException e) {

            status.append('&').append(e.getMessage().replaceAll(",", ";"));
        }
    }

    private void sendOpprettPerson(Person person) {

        pdlForvalterConsumer.postOpprettPerson(mapperFacade.map(person, PdlOpprettPerson.class), person.getIdent());
    }

    private void sendNavn(Person person) {

        pdlForvalterConsumer.postNavn(mapperFacade.map(person, PdlNavn.class), person.getIdent());
    }

    private void sendKjoenn(Person person, boolean isOpprettEndre, List<String> nyePartnereOgBarn) {
        if (!isOpprettEndre || nyePartnereOgBarn.contains(person.getIdent())) {
            pdlForvalterConsumer.postKjoenn(mapperFacade.map(person, PdlKjoenn.class), person.getIdent());
        }
    }

    private void sendAdressebeskyttelse(Person person) {

        pdlForvalterConsumer.postAdressebeskyttelse(mapperFacade.map(person, PdlAdressebeskyttelse.class),
                person.getIdent());
    }

    private void sendFamilierelasjoner(Person person) {

        person.getRelasjoner().forEach(relasjon -> {
            if (!relasjon.isPartner()) {
                pdlForvalterConsumer.postFamilierelasjon(mapperFacade.map(relasjon, PdlFamilierelasjon.class),
                        person.getIdent());
            }
        });
    }

    private void sendSivilstand(Person person) {

        if (person.isMyndig()) {
            pdlForvalterConsumer.postSivilstand(mapperFacade.map(person, PdlSivilstand.class), person.getIdent());
        }
    }

    private void sendDoedsfall(Person person) {

        if (nonNull(person.getDoedsdato())) {
            pdlForvalterConsumer.postDoedsfall(mapperFacade.map(person, PdlDoedsfall.class),
                    person.getIdent());
        }
    }

    private void sendStatsborgerskap(Person person) {

        person.getStatsborgerskap().forEach(statsborgerskap ->
                pdlForvalterConsumer.postStatsborgerskap(mapperFacade.map(statsborgerskap, PdlStatsborgerskap.class),
                        person.getIdent())
        );
    }

    private void sendFoedselsmelding(Person person) {

        pdlForvalterConsumer.postFoedsel(mapperFacade.map(person, PdlFoedsel.class), person.getIdent());
    }

    private void sendTelefonnummer(Person person) {

        PdlTelefonnummer telefonnumre = mapperFacade.map(person, PdlTelefonnummer.class);
        telefonnumre.getTelfonnumre().forEach(telefonnummer ->
                pdlForvalterConsumer.postTelefonnummer(telefonnummer, person.getIdent())
        );
    }

    private void sendOppholdsadresse(Person person) {

        pdlForvalterConsumer.postOppholdsadresse(mapperFacade.map(person, PdlOppholdsadresse.class), person.getIdent());
    }

    private void sendKontaktadresse(Person person) {

        pdlForvalterConsumer.postKontaktadresse(mapperFacade.map(person, PdlKontaktadresse.class), person.getIdent());
    }

    private void sendUtenlandsid(Pdldata pdldata, String ident, StringBuilder status) {

        if (nonNull(pdldata) && nonNull(pdldata.getUtenlandskIdentifikasjonsnummer())) {
            try {
                appendName(UTENLANDS_IDENTIFIKASJONSNUMMER, status);

                List<PdlUtenlandskIdentifikasjonsnummer> utenlandskId = pdldata.getUtenlandskIdentifikasjonsnummer();
                utenlandskId.forEach(id -> {
                    id.setKilde(nullcheckSetDefaultValue(id.getKilde(), CONSUMER));

                    ResponseEntity<JsonNode> response =
                            pdlForvalterConsumer.postUtenlandskIdentifikasjonsnummer(id, ident);

                    appendOkStatus(response.getBody(), status);
                });

            } catch (RuntimeException exception) {

                appendErrorStatus(exception, status);
                log.error(exception.getMessage(), exception);
            }
        }
    }

    private void sendDoedsbo(Pdldata pdldata, String ident, StringBuilder status) {

        if (nonNull(pdldata) && nonNull(pdldata.getKontaktinformasjonForDoedsbo())) {
            try {
                appendName(KONTAKTINFORMASJON_DOEDSBO, status);

                ResponseEntity<JsonNode> response =
                        pdlForvalterConsumer.postKontaktinformasjonForDoedsbo(pdldata.getKontaktinformasjonForDoedsbo(), ident);

                appendOkStatus(response.getBody(), status);

            } catch (RuntimeException exception) {

                appendErrorStatus(exception, status);
                log.error(exception.getMessage(), exception);
            }
        }
    }

    private void sendFalskIdentitet(Pdldata pdldata, String ident, StringBuilder status) {

        if (nonNull(pdldata) && nonNull(pdldata.getFalskIdentitet())) {
            try {
                appendName(FALSK_IDENTITET, status);

                ResponseEntity<JsonNode> response = pdlForvalterConsumer.postFalskIdentitet(pdldata.getFalskIdentitet(), ident);

                appendOkStatus(response.getBody(), status);

            } catch (RuntimeException exception) {

                appendErrorStatus(exception, status);
                log.error(exception.getMessage(), exception);
            }
        }
    }

    private void sendDeleteIdent(TpsPerson tpsPerson) {

        try {
            pdlForvalterConsumer.deleteIdent(tpsPerson.getHovedperson());
            tpsPerson.getPartnere().forEach(pdlForvalterConsumer::deleteIdent);
            tpsPerson.getBarn().forEach(pdlForvalterConsumer::deleteIdent);

        } catch (HttpClientErrorException e) {

            if (!HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                log.error(e.getMessage(), e);
            }
        } catch (RuntimeException e) {

            log.error(e.getMessage(), e);
        }
    }

    private static void appendName(String utenlandsIdentifikasjonsnummer, StringBuilder builder) {
        builder.append('$')
                .append(utenlandsIdentifikasjonsnummer);
    }

    private static void appendOkStatus(JsonNode jsonNode, StringBuilder builder) {
        builder.append("&OK");
        if (nonNull(jsonNode) && nonNull(jsonNode.get(HENDELSE_ID))) {
            builder.append(", ")
                    .append(HENDELSE_ID)
                    .append(": ")
                    .append(jsonNode.get(HENDELSE_ID));
        }
    }

    private void appendErrorStatus(RuntimeException exception, StringBuilder builder) {

        builder.append('&')
                .append(errorStatusDecoder.decodeRuntimeException(exception));
    }
}