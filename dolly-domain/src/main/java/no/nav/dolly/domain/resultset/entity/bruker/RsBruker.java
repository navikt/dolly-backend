package no.nav.dolly.domain.resultset.entity.bruker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.entity.team.RsTeamMedIdOgNavn;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsBruker {
    private String navIdent;
    private List<RsTeamMedIdOgNavn> teams;
    private List<RsTestgruppe> favoritter;

    public List<RsTeamMedIdOgNavn> getTeams() {
        if (teams == null) {
            teams = new ArrayList<>();
        }
        return teams;
    }

    public List<RsTestgruppe> getFavoritter() {
        if (favoritter == null) {
            favoritter = new ArrayList<>();
        }
        return favoritter;
    }
}