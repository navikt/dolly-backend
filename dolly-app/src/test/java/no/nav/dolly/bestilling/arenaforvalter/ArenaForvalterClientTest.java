package no.nav.dolly.bestilling.arenaforvalter;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaArbeidssokerBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;

@RunWith(MockitoJUnitRunner.class)
public class ArenaForvalterClientTest {

    private static final String IDENT = "12423353112";
    private static final String ENV = "q2";
    private static final String ERROR_CAUSE = "Bad request";
    private static final String ERROR_MSG = "An error has occured";

    @Mock
    private ArenaForvalterConsumer arenaForvalterConsumer;

    @InjectMocks
    private ArenaForvalterClient arenaForvalterClient;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private HttpClientErrorException httpClientErrorException;

    @Mock
    private MapperFacade mapperFacade;

    @Before
    public void setup() {
        when(arenaForvalterConsumer.getEnvironments()).thenReturn(ResponseEntity.ok(singletonList(ENV)));
        when(arenaForvalterConsumer.getIdent(IDENT)).thenReturn(ResponseEntity.ok(new ArenaArbeidssokerBruker()));
        when(mapperFacade.map(any(Arenadata.class), eq(ArenaNyBruker.class))).thenReturn(new ArenaNyBruker());
    }

    @Test
    public void gjenopprett_Ok() {

        BestillingProgress progress = new BestillingProgress();
        when(arenaForvalterConsumer.postArenadata(any(ArenaNyeBrukere.class))).thenReturn(ResponseEntity.ok(
                ArenaArbeidssokerBruker.builder()
                        .arbeidsokerList(singletonList(ArenaArbeidssokerBruker.Arbeidssoker.builder()
                                .miljoe(ENV)
                                .status("OK")
                                .build()))
                        .build()));

        arenaForvalterClient.gjenopprett(RsDollyBestilling.builder()
                .arenaforvalter(Arenadata.builder().build())
                .environments(singletonList(ENV))
                .build(), NorskIdent.builder().ident(IDENT).build(), progress);

        assertThat(progress.getArenaforvalterStatus(), is(equalTo("q2$OK")));
        verify(arenaForvalterConsumer).getEnvironments();
        verify(arenaForvalterConsumer).getIdent(IDENT);
        verify(arenaForvalterConsumer).postArenadata(any(ArenaNyeBrukere.class));
    }

    @Test
    public void gjenopprett_Feil() {

        BestillingProgress progress = new BestillingProgress();
        when(arenaForvalterConsumer.postArenadata(any(ArenaNyeBrukere.class))).thenThrow(httpClientErrorException);
        when(httpClientErrorException.getMessage()).thenReturn(format("%s %s", HttpStatus.BAD_REQUEST, ERROR_CAUSE));
        when(httpClientErrorException.getResponseBodyAsString()).thenReturn(ERROR_MSG);

        arenaForvalterClient.gjenopprett(RsDollyBestilling.builder()
                .arenaforvalter(Arenadata.builder().build())
                .environments(singletonList(ENV))
                .build(), NorskIdent.builder().ident(IDENT).build(), progress);

        assertThat(progress.getArenaforvalterStatus(), is(equalTo(
                "q2$Feil: 400 Bad request (An error has occured)")));
        verify(arenaForvalterConsumer).getIdent(IDENT);
        verify(arenaForvalterConsumer).postArenadata(any(ArenaNyeBrukere.class));
    }

    @Test
    public void gjenopprett_EnvironmentForArenaNotSelected() {

        BestillingProgress progress = new BestillingProgress();
        arenaForvalterClient.gjenopprett(RsDollyBestilling.builder()
                        .arenaforvalter(Arenadata.builder().build())
                        .environments(singletonList("t3")).build(),
                NorskIdent.builder().ident(IDENT).build(), progress);

        assertThat(progress.getArenaforvalterStatus(), is(equalTo("t3$Feil: Miljø ikke støttet")));
    }

    @Test
    public void gjenopprett_ArenaForvalterNotIncluded() {

        BestillingProgress progress = new BestillingProgress();
        arenaForvalterClient.gjenopprett(RsDollyBestilling.builder().environments(singletonList(ENV)).build(),
                NorskIdent.builder().ident(IDENT).build(), progress);

        verifyZeroInteractions(arenaForvalterConsumer);
        assertThat(progress.getArenaforvalterStatus(), is(nullValue()));
    }
}