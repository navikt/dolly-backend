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
public class PdlStatsborgerskap extends PdlOpplysning {

    private String landkode;
    private LocalDate gyldigFraOgMed;
    private LocalDate gyldigTilOgMed;
    private LocalDate bekreftelsesdato;
}