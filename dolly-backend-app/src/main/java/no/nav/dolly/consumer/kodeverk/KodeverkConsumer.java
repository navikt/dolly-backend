package no.nav.dolly.consumer.kodeverk;

import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.credentials.KodeverkProxyProperties;
import no.nav.dolly.consumer.kodeverk.domain.KodeverkBetydningerResponse;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.service.TokenService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

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
@Slf4j
public class KodeverkConsumer {

    private static final String KODEVERK_URL_COMPLETE = "/api/v1/kodeverk/{kodeverksnavn}/koder/betydninger";

    private final TokenService tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serverProperties;

    public KodeverkConsumer(TokenService tokenService, KodeverkProxyProperties serverProperties) {
        this.tokenService = tokenService;
        this.serverProperties = serverProperties;
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

    private static String getKodeverksnavnUrl(String kodeverksnavn) {
        return KODEVERK_URL_COMPLETE.replace("{kodeverksnavn}", kodeverksnavn);
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

        log.info("Respons fra kodeverk proxy: {}", Json.pretty(kodeverkResponse.getBody()));
        return kodeverkResponse.getBody().getBetydninger().entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Entry::getKey, KodeverkConsumer::getNorskBokmaal));
    }

    private ResponseEntity<KodeverkBetydningerResponse> getKodeverk(String kodeverk) {

        try {
            AccessToken token = tokenService.generateToken(serverProperties).block();
            if (isNull(token)) {
                throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Feilet å hente accesstoken");
            }
            return webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(getKodeverksnavnUrl(kodeverk.replace(" ", "%20")))
                            .queryParam("ekskluderUgyldige", true)
                            .queryParam("spraak", "nb")
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue())
                    .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                    .header(HEADER_NAV_CALL_ID, generateCallId())
                    .retrieve().toEntity(KodeverkBetydningerResponse.class).block();

        } catch (HttpClientErrorException e) {
            throw new DollyFunctionalException(e.getMessage(), e);
        }
    }
}
