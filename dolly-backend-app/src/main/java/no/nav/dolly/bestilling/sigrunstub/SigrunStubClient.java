package no.nav.dolly.bestilling.sigrunstub;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.sigrunstub.RsOpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;

@Log4j2
@Service
@RequiredArgsConstructor
public class SigrunStubClient implements ClientRegister {

    private final SigrunStubConsumer sigrunStubConsumer;
    private final SigrunStubResponseHandler sigrunStubResponseHandler;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Timed(name = "providers", tags={"operation", "gjenopprettSigrunStub"})
    @Override public void gjenopprett(RsDollyBestillingRequest bestilling, TpsPerson tpsPerson, BestillingProgress progress) {

        if (!bestilling.getSigrunstub().isEmpty()) {
            try {
                for (RsOpprettSkattegrunnlag request : bestilling.getSigrunstub()) {
                    request.setPersonidentifikator(tpsPerson.getHovedperson());
                }

                deleteExistingSkattegrunnlag(bestilling.getSigrunstub().get(0).getPersonidentifikator());

                progress.setSigrunstubStatus(
                        sigrunStubResponseHandler.extractResponse(
                                sigrunStubConsumer.createSkattegrunnlag(bestilling.getSigrunstub())));

            } catch (RuntimeException e) {
                progress.setSigrunstubStatus(errorStatusDecoder.decodeRuntimeException(e));
            }
        }
    }

    private void deleteExistingSkattegrunnlag(String ident) {
        try {
            // Alle skattegrunnlag har samme ident
            sigrunStubConsumer.deleteSkattegrunnlag(ident);

        } catch (HttpClientErrorException error) {
            if (!HttpStatus.NOT_FOUND.equals(error.getStatusCode())) {

                log.error("Feilet å slette ident {} fra Sigrunstub", ident, error);
            }
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(this::deleteExistingSkattegrunnlag);
    }
}
