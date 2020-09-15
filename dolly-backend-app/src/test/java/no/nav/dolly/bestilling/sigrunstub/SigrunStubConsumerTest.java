package no.nav.dolly.bestilling.sigrunstub;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.OidcTokenAuthentication;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@RestClientTest(SigrunStubConsumer.class)
public class SigrunStubConsumerTest {

    private static final String IDENT = "111111111";

    private MockRestServiceServer server;

    @Autowired
    private RestTemplate restTemplate;

    @MockBean
    private ProvidersProps providersProps;

    @Autowired
    private SigrunStubConsumer sigrunStubConsumer;

    private OpprettSkattegrunnlag skattegrunnlag;

    @Before
    public void setup() {
        ProvidersProps.SigrunStub sigrunStub = ProvidersProps.SigrunStub.builder()
                .url("https://localhost:8080").build();
        when(providersProps.getSigrunStub()).thenReturn(sigrunStub);

        skattegrunnlag = OpprettSkattegrunnlag.builder()
                .inntektsaar("1978")
                .build();

        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void createSkattegrunnlag() throws Exception {

        server.expect(requestTo("https://localhost:8080/api/v1/lignetinntekt"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(asJsonString(singletonList(skattegrunnlag))))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        sigrunStubConsumer.createSkattegrunnlag(singletonList(skattegrunnlag));
    }

    @Test(expected = HttpClientErrorException.class)
    public void createSkattegrunnlag_kasterSigrunExceptionHvisKallKasterClientException() throws Exception {

        server.expect(requestTo("https://localhost:8080/api/v1/lignetinntekt"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(asJsonString(singletonList(skattegrunnlag))))
                .andRespond(withBadRequest());

        sigrunStubConsumer.createSkattegrunnlag(singletonList(skattegrunnlag));
    }

    @Test
    public void deletSkattegrunnlag_Ok() {

        server.expect(requestTo("https://localhost:8080/api/v1/slett"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header("personidentifikator", IDENT))
                .andRespond(withSuccess());

        sigrunStubConsumer.deleteSkattegrunnlag(IDENT);
    }

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}