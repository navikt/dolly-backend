package no.nav.dolly.provider.api.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.GET;

import no.nav.dolly.domain.resultset.RsDollyProps;
import no.nav.dolly.provider.RestTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("GET /api/v1/config")
class ConfigGetTest extends RestTestBase {

    private static final String ENDPOINT_BASE_URI = "/api/v1/config";

    @Test
    @DisplayName("Returnerer urler til andre tjenester")
    void shouldGetUrls() {
        RsDollyProps resp = sendRequest()
                .to(GET, ENDPOINT_BASE_URI)
                .andExpect(HttpStatus.OK, RsDollyProps.class);

        assertNotNull(resp);
        assertTrue(resp.getTpsfUrl().contains("tps"));
        assertTrue(resp.getSigrunStubUrl().contains("sigrunstub"));
        assertTrue(resp.getKrrStubUrl().contains("krrstub"));
        assertTrue(resp.getKodeverkUrl().contains("kodeverk"));
        assertTrue(resp.getArenaForvalterUrl().contains("arenaforvalter"));
    }
}
