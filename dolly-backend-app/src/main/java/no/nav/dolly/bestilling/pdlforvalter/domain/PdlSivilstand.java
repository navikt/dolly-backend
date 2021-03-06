package no.nav.dolly.bestilling.pdlforvalter.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdlSivilstand {

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
    private String kilde;
    private String kommune;
    private String master;
    private String myndighet;
    private String relatertVedSivilstand;
    private LocalDate sivilstandsdato;
    private String sted;
    private Sivilstand type;
    private String utland;
}