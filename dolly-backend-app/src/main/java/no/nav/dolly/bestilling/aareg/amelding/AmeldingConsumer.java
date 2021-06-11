package no.nav.dolly.bestilling.aareg.amelding;

import no.nav.dolly.config.credentials.AmeldingServiceProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.service.TokenService;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.AMeldingDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static no.nav.dolly.util.CallIdUtil.generateCallId;

@Service
public class AmeldingConsumer {

    private final TokenService tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serverProperties;

    public AmeldingConsumer(TokenService tokenService, AmeldingServiceProperties serviceProperties) {
        this.tokenService = tokenService;
        this.serverProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "amelding_put" })
    public ResponseEntity<Object> putAmeldingdata(AMeldingDTO amelding) {
        return webClient.put()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/amelding").build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.generateToken(serverProperties).map(AccessToken::getTokenValue))
                .header("Nav-Call-Id", generateCallId())
                .body(BodyInserters.fromPublisher(Mono.just(amelding), AMeldingDTO.class))
                .retrieve()
                .toEntity(Object.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "amelding_get" })
    public ResponseEntity<AMeldingDTO> getAmelding(String id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/amelding/{id}")
                        .build(id))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.generateToken(serverProperties).map(AccessToken::getTokenValue))
                .header("Nav-Call-Id", generateCallId())
                .retrieve()
                .toEntity(AMeldingDTO.class)
                .block();
    }
}
