package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.dolly.domain.resultset.pdlforvalter.PdlOpplysning;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlKontaktinformasjonForDoedsbo extends PdlOpplysning {

    private Adressat adressat;
    private String adresselinje1;
    private String adresselinje2;
    private String landkode;
    private String postnummer;
    private String poststedsnavn;
    private PdlSkifteform skifteform;
    private LocalDate attestutstedelsesdato;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Adressat {

        private PdlAdvokat advokatSomAdressat;
        private PdlKontaktpersonMedIdNummer kontaktpersonMedIdNummerSomAdressat;
        private PdlKontaktpersonUtenIdNummer kontaktpersonUtenIdNummerSomAdressat;
        private PdlOrganisasjon organisasjonSomAdressat;
    }
}
