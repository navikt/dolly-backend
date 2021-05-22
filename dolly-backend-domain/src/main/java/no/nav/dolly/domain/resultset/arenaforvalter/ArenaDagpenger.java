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
        private LocalDate fraDato; // TODO: Endre til dagpenge eller vedtak dato, samme for til og mottatt
        private LocalDate tilDato;
        private LocalDate mottattDato;
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

    public List<NyeDagp> getNyeDagp() {
        if (isNull(nyeDagp)) {
            return new ArrayList<>();
        }
        return nyeDagp;
    }
}