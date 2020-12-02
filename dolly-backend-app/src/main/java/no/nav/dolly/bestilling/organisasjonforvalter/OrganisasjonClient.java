package no.nav.dolly.bestilling.organisasjonforvalter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.OrganisasjonRegister;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonResponse;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisasjonClient implements OrganisasjonRegister {

    private final OrganisasjonConsumer organisasjonConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;

    @Override
    public void gjenopprett(OrganisasjonBestilling bestilling, OrganisasjonBestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getOrganisasjon())) {

            StringBuilder status = new StringBuilder();
            OrganisasjonRequest organisasjonRequest = mapperFacade.map(bestilling.getOrganisasjon(), OrganisasjonRequest.class);

            Arrays.stream(bestilling.getMiljoer().split(",")).forEach(environment -> {

                try {
                    ResponseEntity<OrganisasjonResponse> response = organisasjonConsumer.postOrganisasjon(organisasjonRequest);
                    if (response.hasBody()) {
                        status.append(isNotBlank(status) ? ',' : "")
                                .append(environment)
                                .append(":OK");
                    }

                } catch (RuntimeException e) {

                    status.append(isNotBlank(status) ? ',' : "")
                            .append(environment)
                            .append(':')
                            .append(errorStatusDecoder.decodeRuntimeException(e));

                    log.error("Feilet å legge inn organisasjon: {} til miljø: {}",
                            organisasjonRequest.getHovedOrganisasjon().getOrganisasjonsnavn(), environment, e);
                }
            });
            progress.setOrganisasjonsforvalterStatus(status.toString());
        }
    }

    @Override
    public void release(List<String> identer) {

    }
}
