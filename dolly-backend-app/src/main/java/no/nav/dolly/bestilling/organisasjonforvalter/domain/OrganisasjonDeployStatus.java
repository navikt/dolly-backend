package no.nav.dolly.bestilling.organisasjonforvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonStatusDTO.Status;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganisasjonDeployStatus {

    private String environment;
    private Status status;
    private String details;
    private String error;
}