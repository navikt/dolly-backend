package no.nav.dolly.bestilling.aareg.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.aareg.RsAktoer;
import no.nav.dolly.domain.resultset.aareg.RsAntallTimerIPerioden;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsavtale;
import no.nav.dolly.domain.resultset.aareg.RsFartoy;
import no.nav.dolly.domain.resultset.aareg.RsPeriodeAareg;
import no.nav.dolly.domain.resultset.aareg.RsPersonAareg;
import no.nav.dolly.domain.resultset.aareg.RsUtenlandsopphold;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.PermisjonDTO;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Arbeidsforhold {

    private RsPeriodeAareg ansettelsesPeriode;

    private List<RsAntallTimerIPerioden> antallTimerForTimeloennet;

    private RsArbeidsavtale arbeidsavtale;

    private String arbeidsforholdID;

    private Long arbeidsforholdIDnav;

    private String arbeidsforholdstype;

    private RsAktoer arbeidsgiver;

    private List<RsFartoy> fartoy;

    private RsPersonAareg arbeidstaker;

    private List<PermisjonDTO> permisjon;

    private List<RsUtenlandsopphold> utenlandsopphold;

    public List<RsAntallTimerIPerioden> getAntallTimerForTimeloennet() {
        if (isNull(antallTimerForTimeloennet)) {
            antallTimerForTimeloennet = new ArrayList<>();
        }
        return antallTimerForTimeloennet;
    }

    public List<RsUtenlandsopphold> getUtenlandsopphold() {
        if (isNull(utenlandsopphold)) {
            utenlandsopphold = new ArrayList<>();
        }
        return utenlandsopphold;
    }

    public List<PermisjonDTO> getPermisjon() {
        if (isNull(permisjon)) {
            permisjon = new ArrayList<>();
        }
        return permisjon;
    }
}
