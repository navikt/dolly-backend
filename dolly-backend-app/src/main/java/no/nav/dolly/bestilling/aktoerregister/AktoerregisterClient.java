package no.nav.dolly.bestilling.aktoerregister;

import static java.util.Objects.isNull;
import static no.nav.dolly.domain.CommonKeys.SYNTH_ENV;

import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;

@Slf4j
@Service
@Order(2)
@RequiredArgsConstructor
public class AktoerregisterClient implements ClientRegister {

    private static final int MAX_COUNT = 200;
    private static final int TIMEOUT = 50;

    private final AktoerregisterConsumer aktoerregisterConsumer;

    @Override public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (bestilling.getEnvironments().contains(SYNTH_ENV)) {
            int count = 0;

            try {
                while (count++ < MAX_COUNT &&
                        isNull(aktoerregisterConsumer.getAktoerId(tpsPerson.getHovedperson())
                                .get(tpsPerson.getHovedperson()).get("identer"))) {
                    Thread.sleep(TIMEOUT);
                }

            } catch (InterruptedException | RuntimeException e) {
                log.error("Feilet å lese id fra AKtørregister for ident {}.", tpsPerson.getHovedperson(), e);
            }

            if (count < MAX_COUNT) {
                log.info("Synkronisering mot Aktørregister tok {} ms.", count * TIMEOUT);
            } else {
                log.warn("Synkronisering mot Aktørregister gitt opp etter {} ms.", MAX_COUNT * TIMEOUT);
            }
        }
    }

    @Override public void release(List<String> identer) {

    }
}
