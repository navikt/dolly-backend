package no.nav.dolly.bestilling.arenaforvalter;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaArbeidssokerBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;

@Slf4j
@Service
public class ArenaForvalterClient implements ClientRegister {

    @Autowired
    private ArenaForvalterConsumer arenaForvalterConsumer;

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public void gjenopprett(RsDollyBestillingRequest bestilling, TpsPerson tpsPerson, BestillingProgress progress) {

        if (nonNull(bestilling.getArenaforvalter())) {

            StringBuilder status = new StringBuilder();

            ResponseEntity<List> envResponse = arenaForvalterConsumer.getEnvironments();
            List<String> environments = envResponse.hasBody() ? envResponse.getBody() : emptyList();

            List<String> availEnvironments = new ArrayList(environments);

            availEnvironments.retainAll(bestilling.getEnvironments());

            if (!availEnvironments.isEmpty()) {

                deleteServicebruker(tpsPerson.getHovedperson(), availEnvironments);

                ArenaNyeBrukere arenaNyeBrukere = new ArenaNyeBrukere();
                availEnvironments.forEach(environment -> {
                    ArenaNyBruker arenaNyBruker = mapperFacade.map(bestilling.getArenaforvalter(), ArenaNyBruker.class);
                    arenaNyBruker.setPersonident(tpsPerson.getHovedperson());
                    arenaNyBruker.setMiljoe(environment);
                    arenaNyeBrukere.getNyeBrukere().add(arenaNyBruker);
                });

                sendArenadata(arenaNyeBrukere, status);
            }

            List<String> notSupportedEnvironments = new ArrayList(bestilling.getEnvironments());
            notSupportedEnvironments.removeAll(environments);
            notSupportedEnvironments.forEach(environment ->
                    status.append(',')
                            .append(environment)
                            .append("$Feil: Miljø ikke støttet"));

            if (status.length() > 1) {
                progress.setArenaforvalterStatus(status.substring(1));
            }
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(ident -> {
            ResponseEntity<ArenaArbeidssokerBruker> existingServicebruker = arenaForvalterConsumer.getIdent(ident);
            if (existingServicebruker.hasBody()) {
                existingServicebruker.getBody().getArbeidsokerList().forEach(list -> {
                    if (nonNull(list.getMiljoe())) {
                        newArrayList(list.getMiljoe().split(",")).forEach(
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
                response.getBody().getArbeidsokerList().forEach(arbeidsoker -> {
                    if ("OK".equals(arbeidsoker.getStatus())) {
                        status.append(',')
                                .append(arbeidsoker.getMiljoe())
                                .append('$')
                                .append(arbeidsoker.getStatus());
                    }
                });
                response.getBody().getNyBrukerFeilList().forEach(brukerfeil -> {
                    status.append(',')
                            .append(brukerfeil.getMiljoe())
                            .append('$')
                            .append("Feilstatus: \"")
                            .append(brukerfeil.getNyBrukerFeilstatus())
                            .append("\". Se detaljer i logg.");
                    log.error("Feilet å opprette testperson {} i ArenaForvalter på miljø: {}, feilstatus: {}, melding: \"{}\"",
                            brukerfeil.getPersonident(), brukerfeil.getMiljoe(), brukerfeil.getNyBrukerFeilstatus(), brukerfeil.getMelding());
                });
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
