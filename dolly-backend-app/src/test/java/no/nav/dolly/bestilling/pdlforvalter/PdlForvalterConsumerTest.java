package no.nav.dolly.bestilling.pdlforvalter;

import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlNavn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpprettPerson;
import no.nav.dolly.config.credentials.PdlProxyProperties;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.PdlFalskIdentitet;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.service.TokenService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_PERSON_IDENT;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yaml")
@AutoConfigureWireMock(port = 0)
public class PdlForvalterConsumerTest {

    private static final String IDENT = "11111111111";

    @MockBean
    private TokenService tokenService;

    @MockBean
    private ErrorStatusDecoder errorStatusDecoder;

    @Autowired
    private PdlForvalterConsumer pdlForvalterConsumer;

    @Before
    public void setup() {

        when(tokenService.generateToken(ArgumentMatchers.any(PdlProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @Test
    public void postKontaktinformasjonForDoedsbo_OK() {

        server.expect(requestTo("http://pdl.nav.no/api/v1/bestilling/kontaktinformasjonfordoedsbo"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HEADER_NAV_PERSON_IDENT, IDENT))
                .andRespond(withSuccess());

        pdlForvalterConsumer.postKontaktinformasjonForDoedsbo(PdlKontaktinformasjonForDoedsbo.builder().build(), IDENT);

        verify(tokenService).generateToken(ArgumentMatchers.any(PdlProxyProperties.class));
        verify(stsOidcService).getIdToken(anyString());
    }

    @Test
    public void postUtenlandskIdentifikasjonsnummer_OK() {

        server.expect(requestTo("http://pdl.nav.no/api/v1/bestilling/utenlandsidentifikasjonsnummer"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        pdlForvalterConsumer.postUtenlandskIdentifikasjonsnummer(PdlUtenlandskIdentifikasjonsnummer.builder().build(), IDENT);

        verify(providersProps).getPdlForvalter();
        verify(stsOidcService).getIdToken(anyString());
    }

    @Test
    public void postFalskIdenitet_OK() {

        server.expect(requestTo("http://pdl.nav.no/api/v1/bestilling/falskidentitet"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HEADER_NAV_PERSON_IDENT, IDENT))
                .andRespond(withSuccess());

        pdlForvalterConsumer.postFalskIdentitet(PdlFalskIdentitet.builder().build(), IDENT);

        verify(providersProps).getPdlForvalter();
        verify(stsOidcService).getIdToken(anyString());
    }

    @Test
    public void deleteIdent() {

        server.expect(requestTo("http://pdl.nav.no/api/v1/personident"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header(HEADER_NAV_PERSON_IDENT, IDENT))
                .andRespond(withSuccess());

        pdlForvalterConsumer.deleteIdent(IDENT);

        verify(providersProps).getPdlForvalter();
        verify(stsOidcService).getIdToken(anyString());
    }

    @Test
    public void opprettPerson() {

        server.expect(requestTo("http://pdl.nav.no/api/v1/bestilling/opprettperson"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HEADER_NAV_PERSON_IDENT, IDENT))
                .andRespond(withSuccess());

        pdlForvalterConsumer.postOpprettPerson(PdlOpprettPerson.builder().opprettetIdent(IDENT).build(), IDENT);

        verify(providersProps).getPdlForvalter();
        verify(stsOidcService).getIdToken(anyString());
    }

    @Test
    public void opprettPersonMedIdentHistorikk() {

        server.expect(requestTo("http://pdl.nav.no/api/v1/bestilling/opprettperson?historiskePersonidenter=Person1&historiskePersonidenter=Person2"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HEADER_NAV_PERSON_IDENT, IDENT))
                .andRespond(withSuccess());

        List<String> identHistorikkList = new ArrayList<>();
        identHistorikkList.add("Person1");
        identHistorikkList.add("Person2");

        pdlForvalterConsumer.postOpprettPerson(PdlOpprettPerson.builder().opprettetIdent(IDENT).historiskeIdenter(identHistorikkList).build(), IDENT);

        verify(providersProps).getPdlForvalter();
        verify(stsOidcService).getIdToken(anyString());
    }

    @Test
    public void leggTillNavn() {

        server.expect(requestTo("http://pdl.nav.no/api/v1/bestilling/navn"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HEADER_NAV_PERSON_IDENT, IDENT))
                .andRespond(withSuccess());

        pdlForvalterConsumer.postNavn(PdlNavn.builder().build(), IDENT);
    }
}