package no.nav.dolly.domain.resultset.arenaforvalter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArenaDagpenger {

    private String personident;
    private String miljoe;
    private List<NyeDagp> nyeDagp;


    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NyeDagp {

        private String rettighetKode;
        private LocalDate datoMottatt;
        private Dagpengeperiode dagpengeperiode;
        private GodkjenningerReellArbeidssoker godkjenningerReellArbeidssoker;
        private TaptArbeidstid taptArbeidstid;
        private Vedtaksperiode vedtaksperiode;
        private String vedtaktype;
        private String utfall;

        public List<Vilkaar> getVilkaar() {
            if (isNull(vilkaar)) {
                return new ArrayList<>();
            }
            return vilkaar;
        }

        private List<Vilkaar> vilkaar;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Vilkaar {

        private String kode;
        private String status;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dagpengeperiode {

        private String nullstillPeriodeteller;
        private String nullstillPermitteringsteller;
        private String nullstillPermitteringstellerFisk;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Vedtaksperiode {
        private LocalDate fom;
        private LocalDate tom;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaptArbeidstid {

        private String anvedtRegelKode;
        private Integer fastsattArbeidstid;
        private Integer naavaerendeArbeidstid;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GodkjenningerReellArbeidssoker {
        private String godkjentDeltidssoker;
        private String godkjentLokalArbeidssoker;
        private String godkjentUtdanning;
    }

    public List<NyeDagp> getNyeDagp() {
        if (isNull(nyeDagp)) {
            return new ArrayList<>();
        }
        return nyeDagp;
    }
}