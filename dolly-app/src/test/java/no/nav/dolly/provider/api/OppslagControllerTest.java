package no.nav.dolly.provider.api;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.consumer.kodeverk.KodeverkMapper;
import no.nav.dolly.consumer.norg2.Norg2Consumer;
import no.nav.dolly.consumer.norg2.Norg2EnhetResponse;
import no.nav.dolly.consumer.personoppslag.PersonoppslagConsumer;
import no.nav.dolly.consumer.syntdata.SyntdataConsumer;
import no.nav.dolly.domain.resultset.kodeverk.KodeverkAdjusted;
import no.nav.tjenester.kodeverk.api.v1.Betydning;
import no.nav.tjenester.kodeverk.api.v1.GetKodeverkKoderBetydningerResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class OppslagControllerTest {

    private static final String STANDARD_KODEVERK_NAME = "name";
    private static final String TKNR = "0123";
    private static final String ENHET_NAVN = "Nav Sagene";
    private static final String IDENT = "12345678901";
    private static final String OPPLYSNINGER = "Personopplysninger";
    private static final String PATH = "/test";
    private static final Integer AMOUNT = 1;

    @Mock
    private KodeverkConsumer kodeverkConsumer;

    @Mock
    private KodeverkMapper kodeverkMapper;

    @Mock
    private Norg2Consumer norg2Consumer;

    @InjectMocks
    private OppslagController oppslagController;

    @Mock
    private Betydning betydning;

    @Mock
    private KodeverkAdjusted kodeverkAdjusted;

    @Mock
    private GetKodeverkKoderBetydningerResponse getKodeverkKoderBetydningerResponse;

    @Mock
    private PersonoppslagConsumer personoppslagConsumer;

    @Mock
    private SyntdataConsumer syntdataConsumer;

    @Test
    public void fetchKodeverkByName_happyPath() {
        Map<String, List<Betydning>> betydningerMap = new HashMap<>();
        betydningerMap.put("kode", singletonList(betydning));

        when(kodeverkConsumer.fetchKodeverkByName(STANDARD_KODEVERK_NAME)).thenReturn(getKodeverkKoderBetydningerResponse);
        when(getKodeverkKoderBetydningerResponse.getBetydninger()).thenReturn(betydningerMap);
        when(kodeverkMapper.mapBetydningToAdjustedKodeverk(STANDARD_KODEVERK_NAME, betydningerMap)).thenReturn(kodeverkAdjusted);

        KodeverkAdjusted kodeverkResponse = oppslagController.fetchKodeverkByName(STANDARD_KODEVERK_NAME);

        assertThat(kodeverkResponse, is(kodeverkAdjusted));
    }

    @Test
    public void fetchNorg2Enhet_happyPath() {

        when(norg2Consumer.fetchEnhetByEnhetNr(TKNR)).thenReturn(
                Norg2EnhetResponse.builder()
                        .enhetNr(TKNR)
                        .navn(ENHET_NAVN)
                        .build()
        );

        Norg2EnhetResponse target = oppslagController.fetchEnhetByTknr(TKNR);

        assertThat(target.getEnhetNr(), is(equalTo(TKNR)));
        assertThat(target.getNavn(), is(equalTo(ENHET_NAVN)));
        verify(norg2Consumer).fetchEnhetByEnhetNr(TKNR);
    }

    @Test
    public void oppslagPerson_happyPath() {

        when(personoppslagConsumer.fetchPerson(IDENT)).thenReturn(ResponseEntity.ok(OPPLYSNINGER));

        ResponseEntity response = oppslagController.personoppslag(IDENT);

        verify(personoppslagConsumer).fetchPerson(IDENT);
        assertThat(response.getBody(), is(equalTo(OPPLYSNINGER)));
    }

    @Test
    public void syntdata_happyPath() {

        when(syntdataConsumer.generate(PATH, AMOUNT)).thenReturn(ResponseEntity.ok(OPPLYSNINGER));

        ResponseEntity response = oppslagController.syntdataGenerate(PATH, AMOUNT);

        verify(syntdataConsumer).generate(PATH, AMOUNT);
        assertThat(response.getBody(), is(equalTo(OPPLYSNINGER)));
    }
}