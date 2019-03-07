package no.nav.dolly.domain.resultset;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.sigrunstub.RsOpprettSkattegrunnlag;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyBestilling {

    private List<String> environments;
    private List<RsOpprettSkattegrunnlag> sigrunstub;
    private RsDigitalKontaktdata krrstub;
}
