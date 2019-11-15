package no.nav.dolly.bestilling.inntektsstub;

import static java.lang.String.*;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.inntektsstub.Inntektsinformasjon;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;

@Slf4j
@Service
@RequiredArgsConstructor
public class InntektsstubClient implements ClientRegister {

    private final InntektsstubConsumer inntektsstubConsumer;
    private final MapperFacade mapperFacade;

    @Override
    public void gjenopprett(RsDollyBestillingRequest bestilling, TpsPerson tpsPerson, BestillingProgress progress) {

        if (nonNull(bestilling.getInntektsstub())) {

            Inntektsinformasjon inntektsinformasjon = mapperFacade.map(bestilling.getInntektsstub(), Inntektsinformasjon.class);
            inntektsinformasjon.setNorskIdent(tpsPerson.getHovedperson());

            deleteInntekter(tpsPerson.getHovedperson());
            opprettInntekter(inntektsinformasjon, progress);
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(this::deleteInntekter);
    }

    private void opprettInntekter(Inntektsinformasjon inntektsinformasjon, BestillingProgress progress) {

        try {
            ResponseEntity<Inntektsinformasjon> response = inntektsstubConsumer.postInntekter(inntektsinformasjon);

            if (nonNull(response) && nonNull(response.hasBody())) {

                progress.setInntektsstubStatus(isBlank(response.getBody().getFeilmelding()) ? "OK" : response.getBody().getFeilmelding());

            } else {

                progress.setInntektsstubStatus(format("Feilet å opprette inntekter i Inntektsstub for ident %s.", inntektsinformasjon.getNorskIdent()));
            }

        } catch (HttpClientErrorException e) {

            progress.setInntektsstubStatus(e.getResponseBodyAsString());

        } catch (RuntimeException e) {

            progress.setInntektsstubStatus("Teknisk feil, se logg!");

            log.error("Feilet å opprette inntekter i Inntektsstub for ident {}. Feilmelding: {}", inntektsinformasjon.getNorskIdent(), e.getMessage(), e);
        }

    }

    private void deleteInntekter(String hovedperson) {

        try {
            inntektsstubConsumer.deleteInntekter(hovedperson);
        } catch (HttpClientErrorException | HttpServerErrorException e) {

            log.error("Feilet å slette informasjon om ident {} i Inntektsstub. Feilmelding: {}", hovedperson, e.getResponseBodyAsString());

        } catch (RuntimeException e) {

            log.error("Feilet å slette informasjon om ident {} i Inntektsstub. Feilmelding: {}", hovedperson, e.getMessage());
        }
    }
}
