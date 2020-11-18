package no.nav.dolly.domain.resultset.organisasjon;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsOrganisasjon {

    private List<Organisasjon> organisasjoner;
    private Organisasjon hovedOrganisasjon;

    public enum Orgtype {
        BEDR
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Organisasjon {

        private String organisasjonsnavn;
        private String epost;
        private Orgtype orgtype;
    }
}
