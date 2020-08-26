package no.nav.dolly.bestilling.dokarkiv;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.DOKARKIV;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.dolly.bestilling.dokarkiv.domain.JoarkTransaksjon;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TpsfPersonCache;
import no.nav.dolly.service.TransaksjonMappingService;

@Slf4j
@Service
@RequiredArgsConstructor
public class DokarkivClient implements ClientRegister {

    private final DokarkivConsumer dokarkivConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final TransaksjonMappingService transaksjonMappingService;
    private final ObjectMapper objectMapper;
    private final TpsfPersonCache tpsfPersonCache;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getDokarkiv())) {

            StringBuilder status = new StringBuilder();
            DokarkivRequest dokarkivRequest = mapperFacade.map(bestilling.getDokarkiv(), DokarkivRequest.class);

            tpsfPersonCache.fetchIfEmpty(tpsPerson);
            dokarkivRequest.getBruker().setId(tpsPerson.getHovedperson());
            Person avsender = tpsPerson.getPerson(tpsPerson.getHovedperson());
            dokarkivRequest.getAvsenderMottaker().setId(tpsPerson.getHovedperson());
            dokarkivRequest.getAvsenderMottaker().setNavn(String.format("%s, %s%s", avsender.getFornavn(), avsender.getEtternavn(), isNull(avsender.getMellomnavn()) ? "" : ", " + avsender.getMellomnavn()));

            bestilling.getEnvironments().forEach(environment -> {

                if (!transaksjonMappingService.existAlready(DOKARKIV, tpsPerson.getHovedperson(), environment) || isOpprettEndre) {
                    try {
                        ResponseEntity<DokarkivResponse> response = dokarkivConsumer.postDokarkiv(environment, dokarkivRequest);
                        if (response.hasBody()) {
                            status.append(',')
                                    .append(environment)
                                    .append(":OK");

                            saveTransaksjonsId(requireNonNull(response.getBody()), tpsPerson.getHovedperson(), progress.getBestillingId(), environment);
                        }

                    } catch (RuntimeException e) {

                        status.append(',')
                                .append(environment)
                                .append(':')
                                .append(errorStatusDecoder.decodeRuntimeException(e));

                        log.error("Feilet å legge inn person: {} til Dokarkiv miljø: {}",
                                dokarkivRequest.getBruker().getId(), environment, e);
                    }
                }
            });
            progress.setDokarkivStatus(status.length() > 0 ? status.substring(1) : null);
        }
    }

    @Override
    public void release(List<String> identer) {

    }

    private void saveTransaksjonsId(DokarkivResponse response, String ident, Long bestillingId, String miljoe) {

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(ident)
                        .bestillingId(bestillingId)
                        .transaksjonId(toJson(JoarkTransaksjon.builder()
                                .journalpostId(response.getJournalpostId())
                                .dokumentInfoId(response.getDokumenter().get(0).getDokumentInfoId())
                                .build()))
                        .datoEndret(LocalDateTime.now())
                        .miljoe(miljoe)
                        .system(DOKARKIV.name())
                        .build());
    }

    private String toJson(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere transaksjonsId for dokarkiv", e);
        }
        return null;
    }
}
