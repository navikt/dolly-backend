package no.nav.dolly.domain.jpa;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforhold;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.inntektstub.RsInntektsinformasjon;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.pdlforvalter.RsPdldata;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.NON_EMPTY)
public class BestilteKriterier {

    private List<RsArbeidsforhold> aareg;
	private RsDigitalKontaktdata krrstub;
    private RsUdiPerson udistub;
    private List<OpprettSkattegrunnlag> sigrunstub;
    private Arenadata arenaforvalter;
    private RsPdldata pdlforvalter;
    private List<RsInstdata> instdata;
    private RsInntektsinformasjon inntektsstub;
}
