package no.nav.dolly.domain.resultset.udistub.model.arbeidsadgang;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.UdiHarType;
import no.nav.dolly.domain.resultset.udistub.model.UdiPeriode;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UdiArbeidsadgang {

    private UdiArbeidOmfangType arbeidsOmfang;
    private UdiHarType harArbeidsAdgang;
    private UdiPeriode periode;
    private UdiArbeidsadgangType typeArbeidsadgang;
    private String forklaring;
    private String hjemmel;
}
