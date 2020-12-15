package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.organisasjon.RsSyntetiskeOrganisasjoner;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsOrganisasjonBestilling {

    @Schema(description = "Liste av miljøer bestillingen skal deployes til")
    private List<String> environments;

    private RsSyntetiskeOrganisasjoner rsSyntetiskeOrganisasjoner;
}
