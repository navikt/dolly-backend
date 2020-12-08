package no.nav.dolly.bestilling.krrstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class KrrstubClient implements ClientRegister {

    private final KrrstubConsumer krrstubConsumer;
    private final KrrstubResponseHandler krrstubResponseHandler;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getKrrstub())) {

            try {
                DigitalKontaktdata digitalKontaktdata = mapperFacade.map(bestilling.getKrrstub(), DigitalKontaktdata.class);
                digitalKontaktdata.setPersonident(tpsPerson.getHovedperson());

                kobleSpraakTilMaalform(bestilling, digitalKontaktdata, "NB");
                kobleSpraakTilMaalform(bestilling, digitalKontaktdata, "NN");

                if (!isOpprettEndre) {
                    deleteIdent(tpsPerson.getHovedperson());
                }

                ResponseEntity<Object> krrstubResponse = krrstubConsumer.createDigitalKontaktdata(digitalKontaktdata);
                progress.setKrrstubStatus(krrstubResponseHandler.extractResponse(krrstubResponse));

            } catch (RuntimeException e) {

                progress.setKrrstubStatus(errorStatusDecoder.decodeRuntimeException(e));
                log.error("Kall til KrrStub feilet: {}", e.getMessage(), e);
            }
        }
    }

    private void kobleSpraakTilMaalform(RsDollyUtvidetBestilling bestilling, DigitalKontaktdata digitalKontaktdata, String maalform) {
        if (maalform.equalsIgnoreCase(bestilling.getTpsf().getStatsborgerskap()) && isNull(digitalKontaktdata.getSpraak())) {
            digitalKontaktdata.setSpraak(maalform);
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(this::deleteIdent);
    }

    private void deleteIdent(String ident) {

        try {
            ResponseEntity<DigitalKontaktdata[]> response = krrstubConsumer.getDigitalKontaktdata(ident);

            if (response.hasBody()) {
                List.of(response.getBody()).forEach(dkif -> {
                    if (nonNull(dkif.getId())) {
                        krrstubConsumer.deleteDigitalKontaktdata(dkif.getId());
                    }
                });
            }

        } catch (RuntimeException e) {

            log.error("Feilet å slette ident {} fra KRR-Stub", ident, e);
        }
    }
}
