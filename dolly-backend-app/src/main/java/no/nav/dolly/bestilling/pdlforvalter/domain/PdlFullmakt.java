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

    private String motpartsPersonident;
    private LocalDate gyldigFraOgMed;
    private LocalDate gyldigTilOgMed;
    private List<String> omraader;

}
