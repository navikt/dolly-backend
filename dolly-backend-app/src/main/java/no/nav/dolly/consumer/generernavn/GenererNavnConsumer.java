package no.nav.dolly.consumer.generernavn;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.credentials.GenererNavnServiceProperties;
import no.nav.dolly.consumer.generernavn.command.GenererNavnCommand;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

@Slf4j
@Component
public class GenererNavnConsumer {


    private final TokenService tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serverProperties;

    public GenererNavnConsumer(ProvidersProps providersProps, TokenService tokenService, GenererNavnServiceProperties serverProperties) {
        this.tokenService = tokenService;
        this.serverProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(providersProps.getGenererNavnService().getUrl()).build();
    }

    public ResponseEntity<JsonNode> getPersonnavn(Integer antall) {


        return tokenService.generateToken(serverProperties).flatMap(accessToken ->
                new GenererNavnCommand(webClient, accessToken.getTokenValue(), antall, getNavCallId()).call()
        ).block();

    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}
