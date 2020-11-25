package no.nav.dolly.provider.api.testgruppe;

import no.nav.dolly.domain.jpa.postgres.Testgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsOpprettEndreTestgruppe;
import org.junit.Before;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.LinkedHashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


@DisplayName("PUT /api/v1/gruppe")
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class })
@AutoConfigureMockMvc(addFilters = false)
class TestgruppeControllerPutTest extends TestgruppeTestBase {

    @Test
    @DisplayName("Returnerer HTTP 200 med feilmelding Not Found i body når Testgruppe ikke finnes")
    void shouldFail404WhenTestgruppeDontExist() {

        RsOpprettEndreTestgruppe rsOpprettEndreTestgruppe = RsOpprettEndreTestgruppe.builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .build();

        LinkedHashMap resp = sendRequest(rsOpprettEndreTestgruppe)
                .to(HttpMethod.PUT, ENDPOINT_BASE_URI + "/123")
                .andExpect(HttpStatus.OK, LinkedHashMap.class);

        assertThat(getErrMsg(resp), is("Gruppe med id 123 ble ikke funnet."));
    }

    @Disabled
    @Test
    @DisplayName("Oppdaterer informasjon om Testgruppe")
    void updateTestgruppe() {
        Testgruppe testgruppe = dataFactory.createTestgruppe("Testgruppe");

        RsOpprettEndreTestgruppe rsOpprettEndreTestgruppe = RsOpprettEndreTestgruppe.builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .build();

        Testgruppe resp = sendRequest(rsOpprettEndreTestgruppe)
                .to(HttpMethod.PUT, ENDPOINT_BASE_URI + "/" + testgruppe.getId())
                .andExpect(HttpStatus.OK, Testgruppe.class);

        assertThat(resp.getId(), is(notNullValue()));
        assertThat(resp.getNavn(), is("mingruppe"));
        assertThat(resp.getHensikt(), is("hensikt"));
    }

}
