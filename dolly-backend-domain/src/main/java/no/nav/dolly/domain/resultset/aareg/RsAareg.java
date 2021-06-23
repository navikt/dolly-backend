package no.nav.dolly.domain.resultset.aareg;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class RsAareg {

    private RsPeriodeAareg genererPeriode;

    @Schema(description = "Gyldige verdier finnes i kodeverk 'Arbeidsforholdstyper'")
    private String arbeidsforholdstype;

    private List<RsAaregArbeidsforhold> arbeidsforhold;

    private List<RsAmeldingRequest> amelding;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class RsAaregArbeidsforhold {

        private String arbeidsforholdId;

        private RsPeriodeAareg ansettelsesPeriode;

        private List<RsAntallTimerIPerioden> antallTimerForTimeloennet;

        private RsArbeidsavtale arbeidsavtale;

        private List<RsPermittering> permittering;

        private List<RsPermisjon> permisjon;

        private List<RsFartoy> fartoy;

        private List<RsUtenlandsopphold> utenlandsopphold;

        private RsAktoer arbeidsgiver;
    }
}
