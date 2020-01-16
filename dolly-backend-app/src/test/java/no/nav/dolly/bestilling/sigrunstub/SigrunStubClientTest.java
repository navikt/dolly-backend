package no.nav.dolly.bestilling.sigrunstub;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;

@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class SigrunStubClientTest {

    private static final String IDENT = "11111111";

    @Mock
    private SigrunStubConsumer sigrunStubConsumer;

    @Mock
    private SigrunStubResponseHandler sigrunStubResponseHandler;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @InjectMocks
    private SigrunStubClient sigrunStubClient;

    @Test
    public void gjenopprett_ingendata() {
        BestillingProgress progress = new BestillingProgress();
        sigrunStubClient.gjenopprett(new RsDollyBestillingRequest(), TpsPerson.builder().hovedperson(IDENT).build(), new BestillingProgress());

        assertThat(progress.getSigrunstubStatus(), is(nullValue()));
    }

    @Test
    public void gjenopprett_sigrunstub_feiler() {

        BestillingProgress progress = new BestillingProgress();
        when(sigrunStubConsumer.createSkattegrunnlag(anyList())).thenThrow(HttpClientErrorException.class);
        when(errorStatusDecoder.decodeRuntimeException(any(RuntimeException.class))).thenReturn("Feil:");

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setSigrunstub(singletonList(new OpprettSkattegrunnlag()));

        sigrunStubClient.gjenopprett(request, TpsPerson.builder().hovedperson(IDENT).build(), progress);

        assertThat(progress.getSigrunstubStatus(), containsString("Feil:"));
    }

    @Test
    public void gjenopprett_sigrunstub_ok() {

        BestillingProgress progress = new BestillingProgress();

        when(sigrunStubConsumer.createSkattegrunnlag(anyList())).thenReturn(ResponseEntity.ok(""));
        when(sigrunStubResponseHandler.extractResponse(any())).thenReturn("OK");

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setSigrunstub(singletonList(new OpprettSkattegrunnlag()));
        sigrunStubClient.gjenopprett(request, TpsPerson.builder().hovedperson(IDENT).build(), progress);

        verify(sigrunStubConsumer).createSkattegrunnlag(anyList());
        verify(sigrunStubResponseHandler).extractResponse(any());
        assertThat(progress.getSigrunstubStatus(), is("OK"));
    }
}