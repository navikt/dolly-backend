package no.nav.dolly.bestilling.personservice;

import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.personservice.command.HentAktoerIdCommand;
import no.nav.dolly.bestilling.personservice.domain.AktoerIdent;
import no.nav.dolly.config.credentials.PersonServiceProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

@Service
@Slf4j
public class PersonServiceConsumer {

    private final TokenService tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serverProperties;

    public PersonServiceConsumer(TokenService tokenService, PersonServiceProperties serverProperties) {
        this.tokenService = tokenService;
        this.serverProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl()).build();
    }

    @Timed(name = "providers", tags = { "operation", "aktoerregister_getId" })
    public AktoerIdent getAktoerId(String ident) {

        ResponseEntity<AktoerIdent> response = tokenService.generateToken(serverProperties).flatMap(accessToken ->
                new HentAktoerIdCommand(webClient, accessToken.getTokenValue(), ident, getNavCallId()).call()
        ).block();

        if (isNull(response) || !response.hasBody()) {
            return new AktoerIdent();
        }
        log.info("Response fra PersonService: {}", Json.pretty(response.getBody()));
        return response.getBody();
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}
