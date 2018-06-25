package no.nav.dolly.repository;

import no.nav.dolly.testdata.builder.BrukerBuilder;
import no.nav.dolly.testdata.builder.TeamBuilder;
import no.nav.dolly.testdata.builder.TestgruppeBuilder;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.jpa.Testgruppe;

import java.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TestGruppeRepositoryTest {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    BrukerRepository brukerRepository;

    @Autowired
    TestGruppeRepository testGruppeRepository;

    @Autowired
    IdentRepository identRepository;

    @Test
    public void saveTestgruppeUtenIdenterOgUtenFavorisertAv() {
        Bruker bruker = brukerRepository.save(BrukerBuilder.builder().navIdent("ident").build().convertToRealBruker());

        Team team = teamRepository.save(TeamBuilder.builder()
                .navn("team")
                .datoOpprettet(LocalDate.now())
                .eier(bruker)
                .beskrivelse("besk")
                .build()
                .convertToRealTeam());

        Testgruppe testgruppe = TestgruppeBuilder.builder()
                .sistEndretAv(bruker)
                .datoEndret(LocalDate.of(2000, 1, 1))
                .opprettetAv(bruker)
                .navn("gruppe")
                .teamtilhoerighet(team)
                .build()
                .convertToRealTestgruppe();

        Testgruppe savedGruppe = testGruppeRepository.save(testgruppe);
        Testgruppe foundGruppe = testGruppeRepository.findById(savedGruppe.getId()).get();

        assertThat(foundGruppe.getNavn(), is("gruppe"));
        assertThat(foundGruppe.getTeamtilhoerighet().getNavn(), is("team"));
        assertThat(foundGruppe.getOpprettetAv().getNavIdent(), is("ident"));
    }

}