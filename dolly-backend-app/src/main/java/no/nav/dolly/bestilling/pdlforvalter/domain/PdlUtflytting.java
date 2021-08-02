package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PdlUtflytting extends PdlOpplysning {

    private String tilflyttingsland;
    private String tilflyttingsstedIUtlandet;
    private LocalDate utflyttingsdato;
}