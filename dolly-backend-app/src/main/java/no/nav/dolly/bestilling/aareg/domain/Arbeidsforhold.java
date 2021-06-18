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

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Arbeidsforhold {

    private String arbeidsforholdID;

    private Long arbeidsforholdIDnav;

    private RsPeriodeAareg ansettelsesPeriode;

    private String arbeidsforholdstype;

    private List<RsAntallTimerIPerioden> antallTimerForTimeloennet;

    private RsFartoy fartoy;

    private RsArbeidsavtale arbeidsavtale;

    private List<Permisjon> permisjon;

    private List<Permittering> permittering;

    private List<RsUtenlandsopphold> utenlandsopphold;

    private RsAktoer arbeidsgiver;

    private RsPersonAareg arbeidstaker;

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

    public List<Permisjon> getPermisjon() {
        if (isNull(permisjon)) {
            permisjon = new ArrayList<>();
        }
        return permisjon;
    }
}
