package no.nav.dolly.bestilling.organisasjonforvalter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.OrganisasjonRegister;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonResponse;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.jpa.OrganisasjonNummer;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.OrganisasjonNummerService;
import no.nav.dolly.service.OrganisasjonProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisasjonClient implements OrganisasjonRegister {

    private static int ID_GENERATOR_START = -1;
    private final OrganisasjonConsumer organisasjonConsumer;
    private final OrganisasjonNummerService organisasjonNummerService;
    private final OrganisasjonProgressService organisasjonProgressService;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;

    @Override
    public void opprett(RsOrganisasjonBestilling bestilling, OrganisasjonBestillingProgress progress) {

        StringBuilder status = new StringBuilder();
        OrganisasjonRequest organisasjonRequest = mapperFacade.map(bestilling.getRsOrganisasjoner(), OrganisasjonRequest.class);

        if (nonNull(organisasjonRequest.getOrganisasjoner())) {
            generateIdForOrganisasjoner(organisasjonRequest.getOrganisasjoner(), Integer.toString(ID_GENERATOR_START));
        }

        bestilling.getEnvironments().forEach(environment -> {
            StringBuilder envStatus = new StringBuilder();
            organisasjonRequest.getOrganisasjoner().forEach(organisasjon -> {

                try {
                    ResponseEntity<OrganisasjonResponse> response = organisasjonConsumer.postOrganisasjon(organisasjonRequest);
                    if (response.hasBody()) {
                        envStatus.append(isNotBlank(envStatus) ? ',' : "")
                                .append("OK");

                        organisasjonNummerService.save(OrganisasjonNummer.builder()
                                .bestillingId(progress.getBestillingId())
                                .organisasjonsnr(Objects.requireNonNull(response.getBody()).getOrganisasjonsNummer())
                                .build());

                        organisasjonProgressService.save(progress);
                    }

                } catch (RuntimeException e) {

                    envStatus.append("FEIL");

                    status.append(isNotBlank(status) ? ',' : "")
                            .append(environment)
                            .append(':')
                            .append("FEIL - ")
                            .append(errorStatusDecoder.decodeRuntimeException(e));

                    log.error("Feilet med å legge til organisasjon: {} i miljø: {}",
                            organisasjon, environment, e);
                }
            });
            if (!envStatus.toString().contains("FEIL")) {
                status.append(environment)
                        .append(':')
                        .append("OK");
            }
        });
        progress.setOrganisasjonsforvalterStatus(status.toString());
    }

    private static void generateIdForOrganisasjoner(List<OrganisasjonRequest.Organisasjon> organisasjoner, String parentId) {

        for (OrganisasjonRequest.Organisasjon organisasjon : organisasjoner) {
            ID_GENERATOR_START++;
            organisasjon.setId(Integer.toString(ID_GENERATOR_START));
            organisasjon.setParentId(parentId.equals("-1") ? null : parentId);
            if (nonNull(organisasjon.getUnderenheter()) && !organisasjon.getUnderenheter().isEmpty()) {
                generateIdForOrganisasjoner(organisasjon.getUnderenheter(), organisasjon.getId());
            }
        }
    }

    @Override
    public void release(List<String> orgnummer) {

    }
}
