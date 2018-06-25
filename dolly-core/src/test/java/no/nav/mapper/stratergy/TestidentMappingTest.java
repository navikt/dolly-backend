package no.nav.mapper.stratergy;

import ma.glasnost.orika.MapperFacade;
import no.nav.resultSet.RsTestident;
import no.nav.dolly.testdata.builder.BrukerBuilder;
import no.nav.dolly.testdata.builder.TestgruppeBuilder;
import no.nav.dolly.testdata.builder.TestidentBuilder;
import no.nav.jpa.Bruker;
import no.nav.jpa.Testgruppe;
import no.nav.jpa.Testident;
import no.nav.mapper.utils.MapperTestUtils;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TestidentMappingTest {

    private MapperFacade mapper;

    @Before
    public void setUpHappyPath() {
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new TestgruppeMappingStrategy(), new TeamMappingStrategy());
    }

    @Test
    public void mapToRsTestidentIncludingTestgruppe(){
        Bruker bruker = BrukerBuilder.builder().navIdent("ident").build().convertToRealBruker();

        Testgruppe testgruppe = TestgruppeBuilder.builder()
                .sistEndretAv(bruker)
                .datoEndret(LocalDate.of(2000, 1, 1))
                .opprettetAv(bruker)
                .id(2L)
                .navn("gruppe")
                .build()
                .convertToRealTestgruppe();

        Testident testident = TestidentBuilder.builder().ident(1L).testgruppe(testgruppe).build().convertToRealTestident();

        RsTestident rsTestident = mapper.map(testident, RsTestident.class);

        assertThat(rsTestident.getIdent(), is(1L));
//        assertThat(rsTestident.getTestgruppe().getId(), is(2L));
//        assertThat(rsTestident.getTestgruppe().getNavn(), is("gruppe"));
    }
}
