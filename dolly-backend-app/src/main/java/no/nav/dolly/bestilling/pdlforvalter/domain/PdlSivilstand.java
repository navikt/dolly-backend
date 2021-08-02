package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdlSivilstand extends PdlOpplysning {

    public enum Sivilstand {
        UOPPGITT,
        UGIFT,
        GIFT,
        ENKE_ELLER_ENKEMANN,
        SKILT,
        SEPARERT,
        REGISTRERT_PARTNER,
        SEPARERT_PARTNER,
        SKILT_PARTNER,
        GJENLEVENDE_PARTNER
    }

    private LocalDate bekreftelsesdato;
    private String kommune;
    private String myndighet;
    private String relatertVedSivilstand;
    private LocalDate sivilstandsdato;
    private String sted;
    private Sivilstand type;
    private String utland;
}