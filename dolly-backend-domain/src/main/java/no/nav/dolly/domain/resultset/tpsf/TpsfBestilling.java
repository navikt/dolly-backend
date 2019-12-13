package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.IdentType;
import no.nav.dolly.domain.resultset.tpsf.adresse.AdresseNrInfo;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TpsfBestilling {

    private List<String> opprettFraIdenter;

    private List<String> environments;

    private int antall;

    private RsSimpleRelasjoner relasjoner;

    private IdentType identtype;

    private Integer alder;

    private LocalDateTime foedtEtter;

    private LocalDateTime foedtFoer;

    private RsAdresse boadresse;

    private List<RsPostadresse> postadresse;

    private String kjonn;

    private String statsborgerskap;

    private LocalDateTime statsborgerskapRegdato;

    private String spesreg;

    private LocalDateTime spesregDato;

    private LocalDateTime doedsdato;

    private String sivilstand;

    private LocalDateTime regdato;

    private LocalDateTime egenAnsattDatoFom;

    private LocalDateTime egenAnsattDatoTom;

    private String typeSikkerhetsTiltak;

    private LocalDateTime sikkerhetsTiltakDatoFom;

    private LocalDateTime sikkerhetsTiltakDatoTom;

    private String beskrSikkerhetsTiltak;

    private String sprakKode;

    private LocalDateTime datoSprak;

    private Boolean utenFastBopel;

    private String utvandretTilLand;

    private LocalDateTime utvandretTilLandFlyttedato;

    private Boolean harMellomnavn;

    private String innvandretFraLand;

    private LocalDateTime innvandretFraLandFlyttedato;

    private AdresseNrInfo adresseNrInfo;

    private Boolean erForsvunnet;

    private LocalDateTime forsvunnetDato;

    private List<RsIdenthistorikk> identHistorikk;
}
