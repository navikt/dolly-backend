package no.nav.dolly.consumer.kodeverk;

import no.nav.dolly.config.credentials.KodeverkProxyProperties;
import no.nav.dolly.consumer.kodeverk.domain.KodeverkBetydningerResponse;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.service.TokenService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static no.nav.dolly.config.CachingConfig.CACHE_KODEVERK_2;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;

@Component
public class KodeverkConsumer {

    private static final String KODEVERK_URL_BEGINNING = "/api/v1/kodeverk";
    private static final String KODEVERK_URL_KODER = "koder";
    private static final String KODEVERK_URL_BETYDNINGER = "betydninger";

    private final TokenService tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public KodeverkConsumer(TokenService tokenService, KodeverkProxyProperties serverProperties) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(32 * 1024 * 1024))
                        .build())
                .baseUrl(serverProperties.getUrl()).build();
    }

    private static String getNorskBokmaal(Entry<String, java.util.List<KodeverkBetydningerResponse.Betydning>> entry) {

        return entry.getValue().get(0).getBeskrivelser().get("nb").getTekst();
    }

    @Timed(name = "providers", tags = { "operation", "hentKodeverk" })
    public KodeverkBetydningerResponse fetchKodeverkByName(String kodeverk) {

        var kodeverkResponse = getKodeverk(kodeverk);
        return kodeverkResponse.hasBody() ? kodeverkResponse.getBody() : KodeverkBetydningerResponse.builder().build();
    }

    @Cacheable(CACHE_KODEVERK_2)
    @Timed(name = "providers", tags = { "operation", "hentKodeverk" })
    public Map<String, String> getKodeverkByName(String kodeverk) {

        var kodeverkResponse = getKodeverk(kodeverk);
        if (!kodeverkResponse.hasBody()) {
            return Collections.emptyMap();
        }

        return kodeverkResponse.getBody().getBetydninger().entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Entry::getKey, KodeverkConsumer::getNorskBokmaal));
    }

    private ResponseEntity<KodeverkBetydningerResponse> getKodeverk(String kodeverk) {

        try {
            return webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(KODEVERK_URL_BEGINNING)
                            .pathSegment(kodeverk)
                            .pathSegment(KODEVERK_URL_KODER)
                            .pathSegment(KODEVERK_URL_BETYDNINGER)
                            .queryParam("ekskluderUgyldige", true)
                            .queryParam("spraak", "nb")
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                    .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                    .header(HEADER_NAV_CALL_ID, generateCallId())
                    .retrieve().toEntity(KodeverkBetydningerResponse.class).block();

        } catch (HttpClientErrorException e) {
            throw new DollyFunctionalException(e.getMessage(), e);
        }
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
