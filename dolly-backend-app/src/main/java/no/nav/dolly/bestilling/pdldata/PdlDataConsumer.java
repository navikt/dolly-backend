package no.nav.dolly.bestilling.pdldata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pdldata.command.PdlDataOppdateringCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataOrdreCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataSlettCommand;
import no.nav.dolly.config.credentials.PdlDataForvalterProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.service.TokenService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class PdlDataConsumer {

    private final TokenService tokenService;
    private final WebClient webClient;
    private final PdlDataForvalterProperties serviceProperties;
    private final ObjectMapper objectMapper;

    public PdlDataConsumer(TokenService tokenService, PdlDataForvalterProperties serviceProperties, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
        this.objectMapper = objectMapper;
    }

    @Timed(name = "providers", tags = { "operation", "pdl_sendOrdre" })
    public Flux<String> sendOrdre(OrdreRequestDTO identer) {

        return new PdlDataOrdreCommand(webClient, identer, getAccessToken()).call();
    }

    @Timed(name = "providers", tags = { "operation", "pdl_delete" })
    public void slettPdl(List<String> identer) {

        String accessToken = getAccessToken();
        identer.stream()
                .map(ident -> Flux.from(new PdlDataSlettCommand(webClient, ident, accessToken).call()))
                .reduce(Flux.empty(), Flux::concat)
                .collectList()
                .block();
    }

    public Mono<String> oppdaterPdl(String ident, PersonUpdateRequestDTO request) throws JsonProcessingException {

        var body = objectMapper.writeValueAsString(request);
        return new PdlDataOppdateringCommand(webClient, ident, body, getAccessToken()).call();
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
