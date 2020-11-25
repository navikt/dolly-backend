package no.nav.dolly.bestilling.skjermingsregister;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.skjermingsregister.domain.BestillingPersonWrapper;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataRequest;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TpsfPersonCache;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkjermingsRegisterClient implements ClientRegister {

    private final SkjermingsRegisterConsumer skjermingsRegisterConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final TpsfPersonCache tpsfPersonCache;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        tpsfPersonCache.fetchIfEmpty(tpsPerson);

        if (nonNull(bestilling.getTpsf()) && nonNull(bestilling.getTpsf().getEgenAnsattDatoFom())) {

            StringBuilder status = new StringBuilder();
            for (Person person : tpsPerson.getPersondetaljer()) {
                try {
                    SkjermingsDataRequest skjermingsDataRequest = mapperFacade.map(BestillingPersonWrapper.builder()
                            .bestilling(bestilling.getTpsf())
                            .person(person)
                            .build(),
                            SkjermingsDataRequest.class);
                    if (isAlleredeSkjermet(person) && nonNull(bestilling.getTpsf().getEgenAnsattDatoTom())) {
                        skjermingsRegisterConsumer.putSkjerming(person.getIdent());
                    } else if (!isAlleredeSkjermet(person) && isNull(bestilling.getTpsf().getEgenAnsattDatoTom())) {
                        skjermingsRegisterConsumer.postSkjerming(List.of(skjermingsDataRequest));
                    }
                } catch (RuntimeException e) {
                    status.append(errorStatusDecoder.decodeRuntimeException(e));
                    log.error("Feilet å skjerme person med ident: {}", person.getIdent(), e);
                    break;
                }
            }
            progress.setSkjermingsregisterStatus(isNotBlank(status) ? status.toString() : "OK");
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(skjermingsRegisterConsumer::deleteSkjerming);
    }

    private boolean isAlleredeSkjermet(Person person) {

        try {
            ResponseEntity<SkjermingsDataResponse> skjermingResponseEntity = skjermingsRegisterConsumer.getSkjerming(person.getIdent());
            if (skjermingResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                return true;
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw e;
        }
        return false;
    }
}
