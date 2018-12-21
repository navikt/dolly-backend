package no.nav.dolly.regression.scenarios.rest.testgruppe;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Ignore;
import org.junit.Test;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsOpprettEndreTestgruppe;
import no.nav.dolly.testdata.builder.RsOpprettTestgruppeBuilder;

public class OpprettTestgruppeScenarios extends TestgruppeTestCaseBase {

    @Test
    public void opprettTestgruppeBasertPaaCurrentBruker() throws Exception {
        Team team = teamRepository.findAllByOrderByNavn().get(0);

        RsOpprettEndreTestgruppe rsOpprettEndreTestgruppe = RsOpprettTestgruppeBuilder.builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .teamId(team.getId())
                .build()
                .convertToRealRsOpprettTestgruppe();

        mvcMock.perform(post(endpointUrl)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJson(rsOpprettEndreTestgruppe)))
                .andExpect(status().isCreated())
                .andReturn();

        Testgruppe gruppe = gruppeRepository.findByNavn("mingruppe");

        assertThat(gruppe.getId(), is(notNullValue()));
        assertThat(gruppe.getNavn(), is("mingruppe"));
        assertThat(gruppe.getHensikt(), is("hensikt"));
        assertThat(gruppe.getOpprettetAv().getNavIdent(), is(STANDARD_NAV_IDENT));
    }

    @Test
    @Ignore
    public void opprettTestgruppeUtenAaSpesifisereTeamOgFaaSpesifisertTeamMedNavidentNavn() throws Exception {
        RsOpprettEndreTestgruppe rsOpprettTestgruppe = RsOpprettTestgruppeBuilder.builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .build()
                .convertToRealRsOpprettTestgruppe();

        mvcMock.perform(post(endpointUrl)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJson(rsOpprettTestgruppe)))
                .andExpect(status().isCreated())
                .andReturn();

        Testgruppe gruppe = gruppeRepository.findByNavn("mingruppe");
        Team teamG1 = teamTestRepository.findByIdFetchMedlemmerEagerly(gruppe.getTeamtilhoerighet().getId());
        Bruker brukerG1 = brukerTestRepository.findByNavIdentTeamsEagerly(STANDARD_NAV_IDENT);

        assertThat(gruppe.getId(), is(notNullValue()));
        assertThat(gruppe.getNavn(), is("mingruppe"));
        assertThat(gruppe.getHensikt(), is("hensikt"));
        assertThat(gruppe.getTeamtilhoerighet().getNavn(), is(STANDARD_NAV_IDENT));
        assertThat(gruppe.getOpprettetAv().getNavIdent(), is(STANDARD_NAV_IDENT));

        assertThat(teamG1.getMedlemmer(), hasItem(hasProperty("navIdent", equalTo(STANDARD_NAV_IDENT))));
        assertThat(teamG1.getEier().getNavIdent(), is(STANDARD_NAV_IDENT));

        assertThat(brukerG1.getTeams(), hasItem(hasProperty("navn", equalTo(STANDARD_NAV_IDENT))));
    }

    @Test
    @Ignore
    public void opprettTestgruppeUtenAaSpesifisereTeamOgTeamMedNavidentnavnAlleredeEksisterer() throws Exception {
        RsOpprettEndreTestgruppe rsOpprettEndreTestgruppe = RsOpprettTestgruppeBuilder.builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .build()
                .convertToRealRsOpprettTestgruppe();

        RsOpprettEndreTestgruppe rsOpprettEndreTestgruppe2 = RsOpprettTestgruppeBuilder.builder()
                .navn("mingruppe2")
                .hensikt("hensikt2")
                .build()
                .convertToRealRsOpprettTestgruppe();

        String url = endpointUrl;

        mvcMock.perform(post(url)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJson(rsOpprettEndreTestgruppe)))
                .andExpect(status().isCreated())
                .andReturn();

        mvcMock.perform(post(url)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJson(rsOpprettEndreTestgruppe2)))
                .andExpect(status().isCreated())
                .andReturn();

        Testgruppe gruppe = gruppeRepository.findByNavn("mingruppe");
        Testgruppe gruppe2 = gruppeRepository.findByNavn("mingruppe2");
        Team teamG1 = teamTestRepository.findByIdFetchMedlemmerEagerly(gruppe.getTeamtilhoerighet().getId());

        assertThat(gruppe.getId(), is(notNullValue()));
        assertThat(gruppe.getNavn(), is("mingruppe"));
        assertThat(gruppe.getHensikt(), is("hensikt"));
        assertThat(gruppe.getTeamtilhoerighet().getNavn(), is(STANDARD_NAV_IDENT));
        assertThat(gruppe.getOpprettetAv().getNavIdent(), is(STANDARD_NAV_IDENT));

        assertThat(teamG1.getMedlemmer(), hasItem(hasProperty("navn", equalTo(STANDARD_NAV_IDENT))));

        assertThat(gruppe2.getId(), is(notNullValue()));
        assertThat(gruppe2.getNavn(), is("mingruppe2"));
        assertThat(gruppe2.getHensikt(), is("hensikt2"));
        assertThat(gruppe2.getTeamtilhoerighet().getNavn(), is(STANDARD_NAV_IDENT));
        assertThat(gruppe2.getOpprettetAv().getNavIdent(), is(STANDARD_NAV_IDENT));
    }
}
