package no.nav.dolly.domain.resultset.pdlforvalter.fullmakt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.tpsf.RsSimplePerson;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsFullmakt {

    private long id;
    private RsSimplePerson fullmektig;
    private LocalDate gyldigFom;
    private LocalDate gyldigTom;
    private String kilde;
    private List<String> omraader;
}
