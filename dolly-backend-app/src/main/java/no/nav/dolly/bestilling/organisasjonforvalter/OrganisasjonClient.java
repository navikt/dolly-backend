package no.nav.dolly.bestilling.organisasjonforvalter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.OrganisasjonRegister;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonResponse;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonTransaksjon;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TransaksjonMappingService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.DOKARKIV;
import static no.nav.dolly.domain.resultset.SystemTyper.ORGANISASJON_FORVALTER;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisasjonClient implements OrganisasjonRegister {

    private final OrganisasjonConsumer organisasjonConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final TransaksjonMappingService transaksjonMappingService;
    private final ObjectMapper objectMapper;

    @Override
    public void gjenopprett(OrganisasjonBestilling bestilling, OrganisasjonBestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getOrganisasjon())) {

            StringBuilder status = new StringBuilder();
            OrganisasjonRequest organisasjonRequest = mapperFacade.map(bestilling.getOrganisasjon(), OrganisasjonRequest.class);

            bestilling.getMiljoer().forEach(environment -> {

                if (!transaksjonMappingService.existAlreadyOrg(ORGANISASJON_FORVALTER, bestilling.getOrganisasjon().getHovedOrganisasjon().getOrganisasjonsnavn(), environment) || isOpprettEndre) {
                    try {
                        ResponseEntity<OrganisasjonResponse> response = organisasjonConsumer.postOrganisasjon(organisasjonRequest);
                        if (response.hasBody()) {
                            status.append(isNotBlank(status) ? ',' : "")
                                    .append(environment)
                                    .append(":OK");

                            saveTransaksjonId(requireNonNull(response.getBody()), bestilling.getOrganisasjon().getHovedOrganisasjon().getOrganisasjonsnavn(), progress.getBestillingId(), environment);
                        }

                    } catch (RuntimeException e) {

                        status.append(isNotBlank(status) ? ',' : "")
                                .append(environment)
                                .append(':')
                                .append(errorStatusDecoder.decodeRuntimeException(e));

                        log.error("Feilet å legge inn organisasjon: {} til miljø: {}",
                                organisasjonRequest.getHovedOrganisasjon().getOrganisasjonsnavn(), environment, e);
                    }
                }
            });
            progress.setOrganisasjonsforvalterStatus(status.toString());
        }
    }

    @Override
    public void release(List<String> identer) {

    }

    private void saveTransaksjonId(OrganisasjonResponse response, String ident, Long bestillingId, String miljoe) {

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(ident)
                        .bestillingId(bestillingId)
                        .transaksjonId(toJson(OrganisasjonTransaksjon.builder()
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
