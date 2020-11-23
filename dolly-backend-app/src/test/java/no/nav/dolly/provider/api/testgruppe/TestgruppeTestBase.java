package no.nav.dolly.provider.api.testgruppe;

import no.nav.dolly.provider.RestTestBase;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

abstract class TestgruppeTestBase extends RestTestBase {

    @MockBean
    private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    protected static final String ENDPOINT_BASE_URI = "/api/v1/gruppe";
}
