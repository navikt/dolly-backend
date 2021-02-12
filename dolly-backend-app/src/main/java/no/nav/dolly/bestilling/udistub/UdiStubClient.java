package no.nav.dolly.bestilling.udistub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonWrapper;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonWrapper.Status;
import no.nav.dolly.bestilling.udistub.util.UdiMergeService;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.CurrentAuthentication;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class UdiStubClient implements ClientRegister {

    private final ErrorStatusDecoder errorStatusDecoder;
    private final UdiMergeService udiMergeService;
    private final UdiStubConsumer udiStubConsumer;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getUdistub())) {
            StringBuilder status = new StringBuilder();

            log.info("OID: 1 {}", CurrentAuthentication.getUserId());
            try {
                UdiPersonResponse eksisterendeUdiPerson = udiStubConsumer.getUdiPerson(tpsPerson.getHovedperson());

                UdiPersonWrapper wrapper = udiMergeService.merge(bestilling.getUdistub(), eksisterendeUdiPerson,
                        isOpprettEndre, tpsPerson);

                wrapper.getUdiPerson().setAliaser(udiMergeService.getAliaser(wrapper.getAliasRequest(), bestilling.getEnvironments()));

                sendUdiPerson(wrapper);
                status.append("OK");

            } catch (RuntimeException e) {

                status.append(errorStatusDecoder.decodeRuntimeException(e));
            }

            progress.setUdistubStatus(status.toString());
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(udiStubConsumer::deleteUdiPerson);
    }

    private void sendUdiPerson(UdiPersonWrapper wrapper) {

        if (Status.NEW == wrapper.getStatus()) {
            udiStubConsumer.createUdiPerson(wrapper.getUdiPerson());
        } else {
            udiStubConsumer.updateUdiPerson(wrapper.getUdiPerson());
        }
    }
}
