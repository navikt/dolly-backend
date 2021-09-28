package no.nav.dolly.bestilling.inntektstub;

import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.bestilling.inntektstub.domain.ValiderInntekt;
import no.nav.dolly.config.credentials.InntektstubProxyProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.service.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.AccessControlException;
import java.util.List;

import static java.util.Objects.isNull;

@Service
public class InntektstubConsumer {

    private static final String INNTEKTER_URL = "/api/v2/inntektsinformasjon";
    private static final String DELETE_INNTEKTER_URL = "/api/v2/personer";
    private static final String VALIDER_INNTEKTER_URL = "/api/v2/valider";
    private static final String NORSKE_IDENTER_QUERY = "norske-identer";

    private final WebClient webClient;
    private final TokenService tokenService;
    private final NaisServerProperties serverProperties;

    public InntektstubConsumer(TokenService tokenService, InntektstubProxyProperties serverProperties) {
        this.tokenService = tokenService;
        this.serverProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "inntk_getInntekter" })
    public ResponseEntity<List<Inntektsinformasjon>> getInntekter(String ident) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(INNTEKTER_URL)
                        .queryParam(NORSKE_IDENTER_QUERY, ident)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .retrieve().toEntityList(Inntektsinformasjon.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "inntk_deleteInntekter" })
    public ResponseEntity<Inntektsinformasjon> deleteInntekter(String ident) {

        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(DELETE_INNTEKTER_URL)
                        .queryParam(NORSKE_IDENTER_QUERY, ident)
                        .pathSegment(ident).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .retrieve().toEntity(Inntektsinformasjon.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "inntk_postInntekter" })
    public ResponseEntity<List<Inntektsinformasjon>> postInntekter(List<Inntektsinformasjon> inntektsinformasjon) {

        return
                webClient.post()
                        .uri(uriBuilder -> uriBuilder
                                .path(INNTEKTER_URL)
                                .build())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                        .bodyValue(inntektsinformasjon)
                        .retrieve().toEntityList(Inntektsinformasjon.class)
                        .block();
    }

    @Timed(name = "providers", tags = { "operation", "inntk_validerInntekt" })
    public ResponseEntity<Object> validerInntekter(ValiderInntekt validerInntekt) {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(VALIDER_INNTEKTER_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .bodyValue(validerInntekt)
                .retrieve().toEntity(Object.class)
                .block();
    }

    private String getAccessToken() {
        AccessToken token = tokenService.generateToken(serverProperties).block();
        if (isNull(token)) {
            throw new AccessControlException("Klarte ikke å generere AccessToken for dokarkiv-proxy");
        }
        return token.getTokenValue();
    }
}
