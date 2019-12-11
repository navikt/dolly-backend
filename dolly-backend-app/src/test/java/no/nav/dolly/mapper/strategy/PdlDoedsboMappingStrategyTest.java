package no.nav.dolly.mapper.strategy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.pdlforvalter.mapper.PdlDoedsboMappingStrategy;
import no.nav.dolly.domain.resultset.pdlforvalter.PdlPersonnavn;
import no.nav.dolly.domain.resultset.pdlforvalter.Pdldata;
import no.nav.dolly.domain.resultset.pdlforvalter.RsPdldata;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlAdvokat;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlKontaktpersonMedIdNummer;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlOrganisasjon;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlSkifteform;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.RsPdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.RsPdlKontaktpersonUtenIdNummer;
import no.nav.dolly.mapper.utils.MapperTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class PdlDoedsboMappingStrategyTest {

    private static final String IDENT = "11111111111";
    private static final String FORNAVN = "Hans";
    private static final String MELLOMNAVN = "Petter";
    private static final String ETTERNAVN = "Andersen";
    private static final String ORGANISAJON_NAVN = "Tvilsom praksis AS";
    private static final String ORGANISAJON_NUMMER = "999000123";
    private static final String ADRESSELINJE_1 = "Utmyra 5";
    private static final String ADRESSELINJE_2 = "Postboks 375";
    private static final LocalDate FODSELSDATO = LocalDate.of(1985, 5, 7);
    private static final LocalDate UTSTEDT_DATO = LocalDate.now();
    private static final String LANDKODE = "POL";
    private static final String POSTNUMMER = "2345";
    private static final String POSTSTED = "BYRKJELO";

    private MapperFacade mapperFacade;

    @Before
    public void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new ZonedDateTime2LocalDateCustomMapping(), new PdlDoedsboMappingStrategy());
    }

    @Test
    public void mapDoedsboOrganisasjonSomAdressat() {

        Pdldata result = mapperFacade.map(RsPdldata.builder()
                .kontaktinformasjonForDoedsbo(RsPdlKontaktinformasjonForDoedsbo.builder()
                        .adressat(PdlOrganisasjon.builder()
                                .kontaktperson(PdlPersonnavn.builder()
                                        .fornavn(FORNAVN)
                                        .mellomnavn(MELLOMNAVN)
                                        .etternavn(ETTERNAVN).build())
                                .organisasjonsnavn(ORGANISAJON_NAVN)
                                .organisasjonsnummer(ORGANISAJON_NUMMER).build())
                        .adresselinje1(ADRESSELINJE_1)
                        .adresselinje2(ADRESSELINJE_2)
                        .build()).build(), Pdldata.class);

        assertThat(result.getKontaktinformasjonForDoedsbo().getAdressat().getOrganisasjonSomAdressat().getOrganisasjonsnavn(), is(ORGANISAJON_NAVN));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAdressat().getOrganisasjonSomAdressat().getOrganisasjonsnummer(), is(ORGANISAJON_NUMMER));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAdressat().getOrganisasjonSomAdressat().getKontaktperson().getFornavn(), is(FORNAVN));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAdressat().getOrganisasjonSomAdressat().getKontaktperson().getMellomnavn(), is(MELLOMNAVN));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAdressat().getOrganisasjonSomAdressat().getKontaktperson().getEtternavn(), is(ETTERNAVN));
    }

    @Test
    public void mapDoedsboAdvokatSomAdressat() {

        Pdldata result = mapperFacade.map(RsPdldata.builder()
                .kontaktinformasjonForDoedsbo(RsPdlKontaktinformasjonForDoedsbo.builder()
                        .adressat(PdlAdvokat.builder()
                                .kontaktperson(PdlPersonnavn.builder()
                                        .fornavn(FORNAVN)
                                        .mellomnavn(MELLOMNAVN)
                                        .etternavn(ETTERNAVN).build())
                                .organisasjonsnavn(ORGANISAJON_NAVN)
                                .organisasjonsnummer(ORGANISAJON_NUMMER).build())
                        .build()).build(), Pdldata.class);

        assertThat(result.getKontaktinformasjonForDoedsbo().getAdressat().getAdvokatSomAdressat().getOrganisasjonsnavn(), is(ORGANISAJON_NAVN));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAdressat().getAdvokatSomAdressat().getOrganisasjonsnummer(), is(ORGANISAJON_NUMMER));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAdressat().getAdvokatSomAdressat().getKontaktperson().getFornavn(), is(FORNAVN));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAdressat().getAdvokatSomAdressat().getKontaktperson().getMellomnavn(), is(MELLOMNAVN));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAdressat().getAdvokatSomAdressat().getKontaktperson().getEtternavn(), is(ETTERNAVN));
    }

    @Test
    public void mapDoedsboKontaktpersonMedIdnummerSomAdressat() {

        Pdldata result = mapperFacade.map(RsPdldata.builder()
                .kontaktinformasjonForDoedsbo(RsPdlKontaktinformasjonForDoedsbo.builder()
                        .adressat(PdlKontaktpersonMedIdNummer.builder()
                                .idnummer(IDENT).build())
                        .build()).build(), Pdldata.class);

        assertThat(result.getKontaktinformasjonForDoedsbo().getAdressat().getKontaktpersonMedIdNummerSomAdressat().getIdnummer(), is(IDENT));
    }

    @Test
    public void mapDoedsboKontaktpersonUtenIdnummerSomAdressat() {

        Pdldata result = mapperFacade.map(RsPdldata.builder()
                .kontaktinformasjonForDoedsbo(RsPdlKontaktinformasjonForDoedsbo.builder()
                        .adressat(RsPdlKontaktpersonUtenIdNummer.builder()
                                .navn(PdlPersonnavn.builder()
                                        .fornavn(FORNAVN)
                                        .mellomnavn(MELLOMNAVN)
                                        .etternavn(ETTERNAVN).build())
                                .foedselsdato(FODSELSDATO.atStartOfDay().atZone(ZoneId.systemDefault())).build())
                        .build()).build(), Pdldata.class);

        assertThat(result.getKontaktinformasjonForDoedsbo().getAdressat().getKontaktpersonUtenIdNummerSomAdressat().getFoedselsdato(), is(FODSELSDATO));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAdressat().getKontaktpersonUtenIdNummerSomAdressat().getNavn().getFornavn(), is(FORNAVN));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAdressat().getKontaktpersonUtenIdNummerSomAdressat().getNavn().getMellomnavn(), is(MELLOMNAVN));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAdressat().getKontaktpersonUtenIdNummerSomAdressat().getNavn().getEtternavn(), is(ETTERNAVN));
    }

    @Test
    public void mapDoedsboRemainingAttributes() {

        Pdldata result = mapperFacade.map(RsPdldata.builder()
                .kontaktinformasjonForDoedsbo(RsPdlKontaktinformasjonForDoedsbo.builder()
                        .adresselinje1(ADRESSELINJE_1)
                        .adresselinje2(ADRESSELINJE_2)
                        .utstedtDato(UTSTEDT_DATO.atStartOfDay().atZone(ZoneId.systemDefault()))
                        .skifteform(PdlSkifteform.OFFENTLIG)
                        .landkode(LANDKODE)
                        .postnummer(POSTNUMMER)
                        .poststedsnavn(POSTSTED)
                        .build()).build(), Pdldata.class);

        assertThat(result.getKontaktinformasjonForDoedsbo().getAdresselinje1(), is(ADRESSELINJE_1));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAdresselinje2(), is(ADRESSELINJE_2));
        assertThat(result.getKontaktinformasjonForDoedsbo().getUtstedtDato(), is(UTSTEDT_DATO));
        assertThat(result.getKontaktinformasjonForDoedsbo().getSkifteform(), is(PdlSkifteform.OFFENTLIG));
        assertThat(result.getKontaktinformasjonForDoedsbo().getLandkode(), is(LANDKODE));
        assertThat(result.getKontaktinformasjonForDoedsbo().getPostnummer(), is(POSTNUMMER));
        assertThat(result.getKontaktinformasjonForDoedsbo().getPoststedsnavn(), is(POSTSTED));
    }
}