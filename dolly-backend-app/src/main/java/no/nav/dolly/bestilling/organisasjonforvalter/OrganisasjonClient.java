package no.nav.dolly.bestilling.organisasjonforvalter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.OrganisasjonRegister;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonBestillingRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonBestillingResponse;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonDeployRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonDeployResponse;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.jpa.OrganisasjonNummer;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.service.OrganisasjonNummerService;
import no.nav.dolly.service.OrganisasjonProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;
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
    public void opprett(RsOrganisasjonBestilling bestilling, Long bestillingId) {

        OrganisasjonBestillingProgress progress = new OrganisasjonBestillingProgress();
        OrganisasjonBestillingRequest organisasjonBestillingRequest = mapperFacade.map(bestilling.getRsSyntetiskeOrganisasjoner(), OrganisasjonBestillingRequest.class);
        StringBuilder status = new StringBuilder();
        OrganisasjonRequest organisasjonRequest = mapperFacade.map(bestilling.getRsOrganisasjoner(), OrganisasjonRequest.class);

        if (nonNull(organisasjonRequest.getOrganisasjoner())) {
            generateIdForOrganisasjoner(organisasjonRequest.getOrganisasjoner(), Integer.toString(ID_GENERATOR_START));
        }

        bestilling.getEnvironments().forEach(environment -> organisasjonBestillingRequest.getOrganisasjoner().forEach(organisasjon -> {

            try {
                ResponseEntity<OrganisasjonBestillingResponse> response = organisasjonConsumer.postOrganisasjon(organisasjonBestillingRequest);
                if (response.hasBody()) {

                    String orgnummer = Objects.requireNonNull(response.getBody()).getOrganisasjonsNummer();
                    organisasjonNummerService.save(OrganisasjonNummer.builder()
                            .bestillingId(bestillingId)
                            .organisasjonsnr(orgnummer)
                            .build());

                    orgnumre.add(orgnummer);
                }
            } catch (RuntimeException e) {

                status.append(isNotBlank(status) ? ',' : "")
                        .append(environment)
                        .append(':')
                        .append("FEIL - ")
                        .append(errorStatusDecoder.decodeRuntimeException(e));

                log.error("Feilet med å legge til organisasjon: {} i miljø: {}",
                        organisasjon, environment, e);
            }
        }));
        deployOrganisasjoner(orgnumre, bestilling.getEnvironments(), status);
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
    public void gjenopprett(OrganisasjonBestillingProgress progress, List<String> miljoer) {

        //TODO: Implementer Gjenopprett

        if (nonNull(progress)) {
            organisasjonProgressService.save(progress);
        }
    }

    @Override
    public void release(List<String> orgnummer) {

    }
}
