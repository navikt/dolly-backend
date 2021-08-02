package no.nav.dolly.bestilling.pdlforvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdresse.Bruksenhetstype;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlMatrikkeladresse extends PdlOpplysning {

        private String adressetilleggsnavn;
        private String bruksenhetsnummer;
        private Bruksenhetstype bruksenhetstype;
        private Integer bruksnummer;
        private Integer festenummer;
        private Integer gaardsnummer;
        private String kommunenummer;
        private String postnummer;
        private Integer undernummer;
}
