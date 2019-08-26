package no.nav.dolly.bestilling.krrstub;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

@RunWith(MockitoJUnitRunner.class)
public class KrrstubClientTest {

    private static final String IDENT = "111111111";
    private static final Long BESTILLING_ID = 1L;

    @Mock
    private KrrstubConsumer krrstubConsumer;

    @Mock
    private KrrstubResponseHandler krrStubResponseHandler;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private KrrstubClient krrstubClient;

    @Test
    public void gjenopprett_ingendata() {
        krrstubClient.gjenopprett(new RsDollyBestilling(), NorskIdent.builder().ident(IDENT).build(), new BestillingProgress());

        verify(krrstubConsumer, times(0)).createDigitalKontaktdata(any(DigitalKontaktdata.class));
    }

    @Test
    public void gjenopprett_krrdata_ok() {

        when(mapperFacade.map(any(RsDigitalKontaktdata.class), eq(DigitalKontaktdata.class)))
                .thenReturn(new DigitalKontaktdata());

        when(krrstubConsumer.readDigitalKontaktdata(IDENT)).thenReturn(ResponseEntity.ok(null));
        when(krrstubConsumer.createDigitalKontaktdata(any(DigitalKontaktdata.class))).thenReturn(ResponseEntity.ok(""));

        krrstubClient.gjenopprett(RsDollyBestilling.builder().krrstub(new RsDigitalKontaktdata()).build(),
                NorskIdent.builder().ident(IDENT).build(),
                BestillingProgress.builder().bestillingId(BESTILLING_ID).build());

        verify(krrstubConsumer).createDigitalKontaktdata(any(DigitalKontaktdata.class));
        verify(krrStubResponseHandler).extractResponse(any(ResponseEntity.class));
    }

    @Test
    public void gjenopprett_krrdata_feil() {

        BestillingProgress progress = BestillingProgress.builder().bestillingId(BESTILLING_ID).build();
        when(krrstubConsumer.readDigitalKontaktdata(IDENT)).thenReturn(ResponseEntity.ok(null));
        when(mapperFacade.map(any(RsDigitalKontaktdata.class), eq(DigitalKontaktdata.class)))
                .thenReturn(new DigitalKontaktdata());
        when(krrstubConsumer.createDigitalKontaktdata(any(DigitalKontaktdata.class))).thenThrow(HttpClientErrorException.class);

        krrstubClient.gjenopprett(RsDollyBestilling.builder()
                .krrstub(new RsDigitalKontaktdata())
                .build(), NorskIdent.builder().ident(IDENT).build(), progress);

        verify(krrstubConsumer).createDigitalKontaktdata(any(DigitalKontaktdata.class));
        verify(krrStubResponseHandler, times(0)).extractResponse(any(ResponseEntity.class));

        assertThat(progress.getKrrstubStatus(), containsString("Feil:"));
    }
}