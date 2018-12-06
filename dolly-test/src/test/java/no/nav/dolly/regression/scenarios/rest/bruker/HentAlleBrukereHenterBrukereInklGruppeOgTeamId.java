package no.nav.dolly.regression.scenarios.rest.bruker;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import no.nav.dolly.domain.resultset.RsBrukerTeamAndGruppeIDs;

public class HentAlleBrukereHenterBrukereInklGruppeOgTeamId extends BrukerTestCaseBase{

    @Test
    public void getBruker() throws Exception {

        MvcResult mvcResult = mvcMock.perform(get(endpointUrl))
                .andExpect(status().isOk())
                .andReturn();

        List<RsBrukerTeamAndGruppeIDs> resultat = convertMvcResultToList(mvcResult, RsBrukerTeamAndGruppeIDs.class);

        RsBrukerTeamAndGruppeIDs res = resultat.get(0);

        assertThat(resultat.size(), is(1));
        assertThat(res.getNavIdent(), is(STANDARD_NAV_IDENT));
        assertThat(res.getTeams().size(), is(1));
        assertThat(res.getTeams().get(0).getNavn(), is(STANDARD_TEAM_NAVN));
    }
}
