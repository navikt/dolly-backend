package no.nav.dolly.domain.resultset;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsTestident {

    private String ident;
    private String tpsfSuccessEnv;
    private String krrstubStatus;
    private String sigrunstubStatus;
    private List<RsMeldingStatusIdent> arenaforvalterStatus;
    private List<RsStatusMiljoeIdentForhold> aaregStatus;
    private RsPdlForvalterStatus pdlforvalterStatus;
}
