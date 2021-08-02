package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PdlFullmakt extends PdlOpplysning {

    private String fullmektig;
    private LocalDate gyldigFom;
    private LocalDate gyldigTom;
    private List<String> omraader;

}
