package no.nav.dolly.bestilling.udistub;

import static java.util.Objects.nonNull;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiAlias;
import no.nav.dolly.domain.resultset.udistub.model.UdiAlias;
import no.nav.dolly.domain.resultset.udistub.model.UdiPerson;
import no.nav.dolly.domain.resultset.udistub.model.UdiPersonNavn;
import no.nav.dolly.metrics.Timed;

@Slf4j
@Service
@RequiredArgsConstructor
public class UdiStubClient implements ClientRegister {

    private final UdiStubConsumer udiStubConsumer;
    private final MapperFacade mapperFacade;
    private final TpsfService tpsfService;

    @Override
    @Timed(name = "providers", tags = { "operation", "gjenopprettUdiStub" })
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getUdistub())) {
            StringBuilder status = new StringBuilder();

            try {

                UdiPerson udiPerson = mapperFacade.map(bestilling.getUdistub(), UdiPerson.class);
                udiPerson.setIdent(tpsPerson.getHovedperson());

                createAndSetAliases(udiPerson, bestilling, tpsPerson.getHovedperson());

                if (!isOpprettEndre) {
                    deletePerson(tpsPerson.getHovedperson());
                }

                ResponseEntity<UdiPersonControllerResponse> response = udiStubConsumer.createUdiPerson(udiPerson);
                appendOkStatus(status, response);

            } catch (RuntimeException e) {

                appendErrorStatus(status, e);
                log.error("Gjenopprett feilet for udistubclient: {}", e.getMessage(), e);
            }

            progress.setUdistubStatus(status.toString());
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(this::deletePerson);
    }

    private void createAndSetAliases(UdiPerson person, RsDollyBestilling bestilling, String ident) {

        try {
            RsAliasResponse aliases = createAliases(ident, bestilling.getUdistub().getAliaser(), bestilling.getEnvironments());
            person.setAliaser(mapperFacade.mapAsList(aliases.getAliaser(), UdiAlias.class));
            person.setFoedselsDato(aliases.getHovedperson().getFodselsdato().toLocalDate());
            person.setNavn(mapperFacade.map(aliases.getHovedperson().getNavn(), UdiPersonNavn.class));

        } catch (RuntimeException e) {
            log.error("Feilet å opprette aliaser i TPSF {}", e.getMessage(), e);
        }
    }

    private void deletePerson(String ident) {

        try {
            udiStubConsumer.deleteUdiPerson(ident);

        } catch (HttpClientErrorException e) {

            if (!NOT_FOUND.equals(e.getStatusCode())) {
                log.error("DeleteUdiPerson feilet: {}", e.getMessage(), e);
            }

        } catch (RuntimeException e) {

            log.error("DeleteUdiPerson feilet: {}", e.getMessage(), e);
        }
    }

    private RsAliasResponse createAliases(String ident, List<RsUdiAlias> aliases, List<String> environments) {
        RsAliasRequest aliasRequest = RsAliasRequest.builder()
                .ident(ident)
                .aliaser(mapperFacade.mapAsList(aliases, RsAliasRequest.AliasSpesification.class))
                .environments(environments)
                .build();

        return tpsfService.createAliases(aliasRequest).getBody();
    }

    private static void appendOkStatus(StringBuilder status, ResponseEntity<UdiPersonControllerResponse> postResponse) {
        if (nonNull(postResponse) && postResponse.hasBody()) {
            status.append("OK");
        }
    }

    private static void appendErrorStatus(StringBuilder status, RuntimeException e) {
        status.append("FEIL: ");

        if (e instanceof HttpClientErrorException) {
            status.append(" (")
                    .append(((HttpClientErrorException) e).getResponseBodyAsString())
                    .append(')');

        } else if (e instanceof HttpServerErrorException) {
            status.append(" Teknisk feil i UdiStub. Se logg!");

        }
    }
}
