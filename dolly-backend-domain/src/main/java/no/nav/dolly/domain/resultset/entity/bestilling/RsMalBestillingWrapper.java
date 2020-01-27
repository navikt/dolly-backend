package no.nav.dolly.domain.resultset.entity.bestilling;

import static java.util.Objects.isNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforhold;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.pdlforvalter.RsPdldata;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;
import springfox.documentation.spring.web.json.Json;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsMalBestillingWrapper {

    private Map<String, Set<RsMalBestilling>> malbestillinger;

    public Map<String, Set<RsMalBestilling>> getMalbestillinger() {

        if (isNull(malbestillinger)) {
            malbestillinger = new TreeMap();
        }
        return malbestillinger;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class RsMalBestilling {

        private Long id;
        private String malNavn;
        private RsBestilling bestilling;
        private String brukerId;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class RsBestilling {

        private Integer antallIdenter;
        private String opprettFraIdenter;
        private List<String> environments;
        private Json tpsf;

        private RsPdldata pdlforvalter;
        private RsDigitalKontaktdata krrstub;
        private List<RsInstdata> instdata;
        private List<RsArbeidsforhold> aareg;
        private List<OpprettSkattegrunnlag> sigrunstub;
        private InntektMultiplierWrapper inntektstub;
        private Arenadata arenaforvalter;
        private RsUdiPerson udistub;
    }
}
