package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsOrganisasjonBestilling {

    @Schema(description = "Liste av miljøer bestillingen skal deployes til")
    private List<String> environments;

    public List<String> getEnvironments() {
        return isNull(environments) ? new ArrayList<>() : environments;
    }

    private List<SyntetiskOrganisasjon> organisasjon;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class SyntetiskOrganisasjon {

        private String enhetstype;
        private String naeringskode;
        private String formaal;
        private String telefon;
        private String epost;
        private String nettside;

        private Adresse forretningsadresse;
        private Adresse postadresse;

        private List<SyntetiskOrganisasjon> underenheter;

        public List<SyntetiskOrganisasjon> getUnderenheter() {
            return isNull(underenheter) ? new ArrayList<>() : underenheter;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class Adresse {

            private List<String> adresselinjer;
            private String postnr;
            private String kommunenr;
            private String landkode;
        }
    }

}