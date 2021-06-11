package no.nav.dolly.bestilling.aareg;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.aareg.amelding.AmeldingConsumer;
import no.nav.dolly.bestilling.aareg.domain.AaregOpprettRequest;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdResponse;
import no.nav.dolly.bestilling.aareg.util.AaregMergeUtil;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.AMeldingDTO;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.Objects.nonNull;

@Slf4j
@Order(3)
@Service
@RequiredArgsConstructor
public class AaregClient implements ClientRegister {

    private final AaregConsumer aaregConsumer;
    private final AmeldingConsumer ameldingConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        StringBuilder result = new StringBuilder();

        if (!bestilling.getAareg().isEmpty()) {
            log.info("Aareg bestilling: " + Json.pretty(bestilling.getAareg()));

            bestilling.getEnvironments().forEach(env -> {

                try {
                    if (nonNull(bestilling.getAareg().get(0).getAmelding()) && !bestilling.getAareg().get(0).getAmelding().isEmpty()) {
                        log.info("Sender a-melding: \n" + Json.pretty(bestilling.getAareg().get(0).getAmelding().get(0)));
                        bestilling.getAareg().get(0).getAmelding().forEach(amelding -> {
                            MappingContext context = new MappingContext.Factory().getContext();
                            context.setProperty("personIdent", dollyPerson.getHovedperson());
                            context.setProperty("arbeidsforholdstype", bestilling.getAareg().get(0).getArbeidsforholdstype());
                            AMeldingDTO ameldingDto = mapperFacade.map(amelding, AMeldingDTO.class, context);
                            log.info("Sender Amelding til service: " + Json.pretty(ameldingDto));
                            ResponseEntity<?> response = ameldingConsumer.putAmeldingdata(ameldingDto);
                            log.info("Response fra Amelding service: " + Json.pretty(response));
                            appendResult((singletonMap(env, "OK")), "1", result);
                        });
                    } else {

                        List<Arbeidsforhold> arbeidsforholdRequest =
                                nonNull(bestilling.getAareg().get(0).getArbeidsforhold()) ? mapperFacade.mapAsList(bestilling.getAareg().get(0).getArbeidsforhold(), Arbeidsforhold.class) : emptyList();
                        List<ArbeidsforholdResponse> eksisterendeArbeidsforhold = aaregConsumer.hentArbeidsforhold(dollyPerson.getHovedperson(), env);


                        List<Arbeidsforhold> arbeidsforhold = AaregMergeUtil.merge(
                                arbeidsforholdRequest,
                                eksisterendeArbeidsforhold,
                                dollyPerson.getHovedperson(), isOpprettEndre);

                        arbeidsforhold.forEach(arbforhold -> {
                            AaregOpprettRequest aaregOpprettRequest = AaregOpprettRequest.builder()
                                    .arbeidsforhold(arbforhold)
                                    .environments(singletonList(env))
                                    .build();
                            log.info("Sender Arbeidsforhold til Aareg: " + Json.pretty(aaregOpprettRequest));
                            appendResult(aaregConsumer.opprettArbeidsforhold(aaregOpprettRequest).getStatusPerMiljoe(), arbforhold.getArbeidsforholdID(), result);
                        });

                        if (arbeidsforhold.isEmpty()) {
                            appendResult(singletonMap(env, "OK"), "0", result);
                        }
                    }
                } catch (RuntimeException e) {
                    Map<String, String> status = new HashMap<>();
                    status.put(env, errorStatusDecoder.decodeRuntimeException(e));
                    appendResult(status, "1", result);
                }
            });
        }

        progress.setAaregStatus(result.length() > 1 ? result.substring(1) : null);
    }

    @Override
    public void release(List<String> identer) {
        identer.forEach(aaregConsumer::slettArbeidsforholdFraAlleMiljoer);
    }

    private static StringBuilder appendResult(Map<String, String> result, String arbeidsforholdId, StringBuilder builder) {
        for (Map.Entry<String, String> entry : result.entrySet()) {
            builder.append(',')
                    .append(entry.getKey())
                    .append(": arbforhold=")
                    .append(arbeidsforholdId)
                    .append('$')
                    .append(entry.getValue().replaceAll(",", "&").replaceAll(":", "="));
        }
        return builder;
    }
}