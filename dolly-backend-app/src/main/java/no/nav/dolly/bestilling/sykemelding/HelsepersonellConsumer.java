package no.nav.dolly.bestilling.sykemelding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sykemelding.domain.dto.HelsepersonellListeDTO;
import no.nav.dolly.config.RemoteApplicationsProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.OnBehalfOfGenerateAccessTokenService;
import no.nav.dolly.security.domain.AccessScopes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class HelsepersonellConsumer {

    private static final String HELSEPERSONELL_URL = "/api/v1/helsepersonell";
    private static final String HELSEPERSONELL_NAME = "testnorge-helsepersonell-api";

    private final ProvidersProps providersProps;
    private final OnBehalfOfGenerateAccessTokenService accessTokenService;
    private final RemoteApplicationsProperties properties;

    private final WebClient webclient = WebClient.builder().baseUrl("${providers.helsepersonell.url}").build();


    @Timed(name = "providers", tags = {"operation", "leger-hent"})
    public ResponseEntity<HelsepersonellListeDTO> getHelsepersonell() {

        return webclient
                .get()
                .uri(URI.create(providersProps.getHelsepersonell().getUrl() + HELSEPERSONELL_URL))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " +
                        accessTokenService.generateToken(
                                new AccessScopes("api://" + properties.getApplications().get(HELSEPERSONELL_NAME) + "//.default"))
                                .getTokenValue())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .toEntity(HelsepersonellListeDTO.class)
                .block();
    }
}
