package no.nav.dolly.bestilling.pdlforvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class PdlAdresse extends PdlOpplysning {

    public enum Adressegradering {UGRADERT, KLIENTADRESSE, FORTROLIG, STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND}

    public enum Bruksenhetstype {BOLIG, ANNET_ENN_BOLIG, FRITIDSBOLIG, IKKE_GODKJENT_BOLIG, UNUMMERERT_BRUKSENHET}

    public enum OppholdAnnetSted {MILITAER, UTENRIKS, PAA_SVALBARD, PENDLER}

    private String adresseIdentifikatorFraMatrikkelen;
    private Adressegradering adressegradering;
    private LocalDate gyldigFraOgMed;
    private LocalDate gyldigTilOgMed;
    private String coAdressenavn;

    private String naerAdresseIdentifikatorFraMatrikkelen;
    private PdlVegadresse vegadresse;
}
