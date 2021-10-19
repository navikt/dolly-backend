package no.nav.dolly.bestilling.dokarkiv;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.dolly.config.credentials.DokarkivProxyServiceProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.service.TokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
public class DokarkivConsumer {

    private final WebClient webClient;
    private final TokenService tokenService;
    private final NaisServerProperties serviceProperties;

    public DokarkivConsumer(DokarkivProxyServiceProperties properties, TokenService tokenService) {
        this.serviceProperties = properties;
        this.tokenService = tokenService;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "dokarkiv-opprett" })
    public Mono<DokarkivResponse> postDokarkiv(String environment, DokarkivRequest dokarkivRequest) {

        String callId = getNavCallId();
        log.info("Sender dokarkiv melding: callId: {}, consumerId: {}, miljø: {}", callId, CONSUMER, environment);

        return webClient.post()
                .uri(builder -> builder.path("/api/{miljo}/v1/journalpost").build(environment))
                .header(AUTHORIZATION, getAccessToken())
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(dokarkivRequest)
                .retrieve()
                .bodyToMono(DokarkivResponse.class)
                .doOnError(error -> {
                    if (error instanceof WebClientResponseException) {
                        log.error(
                                "Feil ved opprettelse av journalpost av med body: {}.",
                                ((WebClientResponseException) error).getResponseBodyAsString(),
                                error
                        );
                    } else {
                        log.error("Feil ved opprettelse av journalpost.", error);
                    }
                });

    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }

    private String getAccessToken() {
        AccessToken token = tokenService.generateToken(serviceProperties).block();
        if (isNull(token)) {
            throw new SecurityException(String.format("Klarte ikke å generere AccessToken for %s", serviceProperties.getName()));
        }
        return "Bearer " + token.getTokenValue();
    }

    public Map<String, String> checkAlive() {
        try {
            return Map.of(serviceProperties.getName(), serviceProperties.checkIsAlive(webClient, getAccessToken()));
        } catch (SecurityException | WebClientResponseException ex) {
            return Map.of(serviceProperties.getName(), String.format("%s, URL: %s", ex.getMessage(), serviceProperties.getUrl()));
        }
    }
}
