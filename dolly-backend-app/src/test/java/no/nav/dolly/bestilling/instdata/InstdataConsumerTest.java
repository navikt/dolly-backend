package no.nav.dolly.bestilling.instdata;

import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.properties.ProvidersProps;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@RestClientTest(InstdataConsumer.class)
public class InstdataConsumerTest {

    private static final String IDENT = "12345678901";
    private static final String ENVIRONMENT = "U2";

    @MockBean
    private ProvidersProps providersProps;

    private MockRestServiceServer server;

    @Autowired
    private InstdataConsumer instdataConsumer;

    @Autowired
    private RestTemplate restTemplate;

    @Before
    public void setup() {
        when(providersProps.getInstdata()).thenReturn(
                ProvidersProps.Instdata.builder()
                        .url("localhost")
                        .build());

        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void deleteInstdata() {

        server.expect(requestTo("localhost/api/v1/ident/batch?identer=12345678901&miljoe=U2"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        instdataConsumer.deleteInstdata(IDENT, ENVIRONMENT);

        verify(providersProps).getInstdata();
    }

    @Test
    public void postInstdata() {

        server.expect(requestTo("localhost/api/v1/ident/batch?miljoe=U2"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        instdataConsumer.postInstdata(newArrayList(Instdata.builder().build()), ENVIRONMENT);

        verify(providersProps).getInstdata();
    }
}