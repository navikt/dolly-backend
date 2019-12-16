package no.nav.dolly.domain.resultset.udistub.model.opphold;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class RsUdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum {

    private RsUdiAvslagEllerBortfall avslagEllerBortfall;
    private UdiOverigIkkeOppholdKategoriType ovrigIkkeOppholdsKategoriArsak;
    private RsUdiUtvistMedInnreiseForbud utvistMedInnreiseForbud;
}