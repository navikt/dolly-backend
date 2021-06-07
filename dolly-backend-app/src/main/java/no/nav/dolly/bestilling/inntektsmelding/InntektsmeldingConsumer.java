package no.nav.dolly.bestilling.inntektsmelding;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

import no.nav.dolly.bestilling.inntektsmelding.commnad.OpprettInntektsmeldingCommand;
import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingRequest;
import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingResponse;
import no.nav.dolly.config.credentials.InntektsmeldingServiceProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.service.TokenService;

@Slf4j
@Service
public class InntektsmeldingConsumer {

    private final TokenService tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serverProperties;

    public InntektsmeldingConsumer(TokenService tokenService, InntektsmeldingServiceProperties serviceProperties) {
        this.tokenService = tokenService;
        this.serverProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "inntektsmelding_opprett"})
    public ResponseEntity<InntektsmeldingResponse> postInntektsmelding(InntektsmeldingRequest inntekstsmelding) {
        String callId = getNavCallId();
        log.info("Inntektsmelding med callId {} sendt", callId);

        return tokenService.generateToken(serverProperties).flatMap(accessToken ->
                new OpprettInntektsmeldingCommand(webClient, accessToken.getTokenValue(), inntekstsmelding).call()
        ).block();
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}
