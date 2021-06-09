package no.nav.dolly.bestilling.sykemelding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.dolly.bestilling.sykemelding.domain.dto.HelsepersonellListeDTO;
import no.nav.dolly.config.credentials.HelsepersonellServiceProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.service.TokenService;

@Slf4j
@Service
public class HelsepersonellConsumer {

    private static final String HELSEPERSONELL_URL = "/api/v1/helsepersonell";

    private final TokenService accessTokenService;
    private final HelsepersonellServiceProperties serviceProperties;
    private final WebClient webClient;

    public HelsepersonellConsumer(
            TokenService accessTokenService,
            HelsepersonellServiceProperties serviceProperties
    ) {
        this.accessTokenService = accessTokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient
                .builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "leger-hent"})
    public ResponseEntity<HelsepersonellListeDTO> getHelsepersonell() {

        log.info("Henter helsepersonell...");
        return accessTokenService.generateToken(serviceProperties).flatMap(accessToken -> webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(HELSEPERSONELL_URL).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getTokenValue())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .toEntity(HelsepersonellListeDTO.class)
        ).block();
    }
}
