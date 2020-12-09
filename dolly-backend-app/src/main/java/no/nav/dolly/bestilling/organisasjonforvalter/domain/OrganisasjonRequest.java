package no.nav.dolly.bestilling.organisasjonforvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.organisasjon.RsOrganisasjoner;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganisasjonRequest {

    private List<RsOrganisasjoner.Organisasjon> organisasjoner;
    private RsOrganisasjoner.Organisasjon hovedOrganisasjon;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Organisasjon {

        private String organisasjonsnavn;
        private String epost;
        private RsOrganisasjoner.Orgtype orgtype;
    }
}
