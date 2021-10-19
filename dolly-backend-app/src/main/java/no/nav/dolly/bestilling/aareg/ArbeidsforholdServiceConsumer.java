package no.nav.dolly.bestilling.aareg;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.aareg.credentials.ArbeidsforholdServiceProperties;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdResponse;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.service.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;

@Slf4j
@Component
public class ArbeidsforholdServiceConsumer {

    private static final String ARBEIDSTAKER_URL = "/api/v2/arbeidstaker";
    private static final String HENT_ARBEIDSFORHOLD = "%s" + ARBEIDSTAKER_URL + "/%s/arbeidsforhold";

    private final TokenService tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public ArbeidsforholdServiceConsumer(TokenService tokenService, ArbeidsforholdServiceProperties serviceProperties) {
        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "aareg_getArbeidforhold" })
    public List<ArbeidsforholdResponse> hentArbeidsforhold(String ident, String miljoe) {

        try {
            String tokenValue = getAccessToken();

            ResponseEntity<List<ArbeidsforholdResponse>> response = webClient.get()
                    .uri(URI.create(format(HENT_ARBEIDSFORHOLD, serviceProperties.getUrl(), ident)))
                    .header(HttpHeaders.AUTHORIZATION, tokenValue)
                    .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                    .header("miljo", miljoe)
                    .header("Nav-Call-Id", getNavCallId())
                    .retrieve()
                    .toEntityList(ArbeidsforholdResponse.class).block();

            return response.hasBody() ? response.getBody() : emptyList();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return emptyList();
            } else {
                throw e;
            }
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

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}