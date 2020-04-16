package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlKontaktinformasjonForDoedsbo {

    private Adressat adressat;
    private String adresselinje1;
    private String adresselinje2;
    private String kilde;
    private String landkode;
    private String postnummer;
    private String poststedsnavn;
    private PdlSkifteform skifteform;
    private LocalDate utstedtDato;

    @Getter
    @Setter
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
