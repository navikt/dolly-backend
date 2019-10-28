package no.nav.dolly.bestilling.arenaforvalter;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaArbeidssokerBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArenaForvalterClient implements ClientRegister {

    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final MapperFacade mapperFacade;

    @Override
    @Timed(name = "providers", tags={"operation", "gjenopprettArenaForvalter"})
    public void gjenopprett(RsDollyBestillingRequest bestilling, TpsPerson tpsPerson, BestillingProgress progress) {

        if (bestilling.getArenaforvalter() == null) {
            progress.setArenaforvalterStatus(null);
            return;
        }

        StringBuilder status = new StringBuilder();
        List<String> environments = new ArrayList<>(arenaForvalterConsumer.getEnvironments());
        environments.retainAll(bestilling.getEnvironments());

            if (!environments.isEmpty()) {

                deleteServicebruker(tpsPerson.getHovedperson(), environments);

                ArenaNyeBrukere arenaNyeBrukere = new ArenaNyeBrukere();
                environments.forEach(environment -> {
                    ArenaNyBruker arenaNyBruker = mapperFacade.map(bestilling.getArenaforvalter(), ArenaNyBruker.class);
                    arenaNyBruker.setPersonident(tpsPerson.getHovedperson());
                    arenaNyBruker.setMiljoe(environment);
                    arenaNyeBrukere.getNyeBrukere().add(arenaNyBruker);
                });

            sendArenadata(arenaNyeBrukere, status);
        }

        List<String> notSupportedEnvironments = new ArrayList<>(bestilling.getEnvironments());
        notSupportedEnvironments.removeAll(environments);
        notSupportedEnvironments.forEach(environment ->
                status.append(',')
                        .append(environment)
                        .append("$Feil: Miljø ikke støttet"));

        progress.setArenaforvalterStatus(status.substring(1));
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(ident -> {
            ResponseEntity<ArenaArbeidssokerBruker> existingServicebruker = arenaForvalterConsumer.getIdent(ident);
            if (existingServicebruker.hasBody()) {
                existingServicebruker.getBody().getArbeidsokerList().forEach(list -> {
                    if (nonNull(list.getMiljoe())) {
                        asList(list.getMiljoe().split(",")).forEach(
                                environment -> arenaForvalterConsumer.deleteIdent(ident, environment));
                    }
                });
            }
        });
    }

    private void deleteServicebruker(String ident, List<String> availEnvironments) {

        try {
            availEnvironments.forEach(environment ->
                    arenaForvalterConsumer.deleteIdent(ident, environment));

        } catch (RuntimeException e) {

            log.error("Feilet å inaktivere testperson: {} i ArenaForvalter: ", ident, e);
        }

    }

    private void sendArenadata(ArenaNyeBrukere arenaNyeBrukere, StringBuilder status) {

        try {
            ResponseEntity<ArenaNyeBrukereResponse> response = arenaForvalterConsumer.postArenadata(arenaNyeBrukere);
            if (response.hasBody()) {
                if (nonNull((response.getBody().getArbeidsokerList()))) {
                    response.getBody().getArbeidsokerList().forEach(arbeidsoker -> {
                        if ("OK".equals(arbeidsoker.getStatus())) {
                            status.append(',')
                                    .append(arbeidsoker.getMiljoe())
                                    .append('$')
                                    .append(arbeidsoker.getStatus());
                        }
                    });
                }
                if (nonNull(response.getBody().getNyBrukerFeilList())) {
                    response.getBody().getNyBrukerFeilList().forEach(brukerfeil -> {
                        status.append(',')
                                .append(brukerfeil.getMiljoe())
                                .append("$Feilstatus: \"")
                                .append(brukerfeil.getNyBrukerFeilstatus())
                                .append("\". Se detaljer i logg.");
                        log.error("Feilet å opprette testperson {} i ArenaForvalter på miljø: {}, feilstatus: {}, melding: \"{}\"",
                                brukerfeil.getPersonident(), brukerfeil.getMiljoe(), brukerfeil.getNyBrukerFeilstatus(), brukerfeil.getMelding());
                    });
                }
            }

        } catch (RuntimeException e) {

            arenaNyeBrukere.getNyeBrukere().forEach(bruker -> {
                status.append(',')
                        .append(bruker.getMiljoe())
                        .append('$');
                appendErrorText(status, e);
            });
            log.error("Feilet å legge inn ny testperson i ArenaForvalter: ", e);
        }
    }

    private static void appendErrorText(StringBuilder status, RuntimeException e) {
        status.append("Feil: ")
                .append(e.getMessage());

        if (e instanceof HttpClientErrorException) {
            status.append(" (")
                    .append(((HttpClientErrorException) e).getResponseBodyAsString().replace(',', '='))
                    .append(')');
        }
    }
}
