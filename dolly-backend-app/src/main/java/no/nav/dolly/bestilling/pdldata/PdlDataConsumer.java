package no.nav.dolly.bestilling.pdldata;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pdldata.command.PdlDataOppdateringCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataOpprettingCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataOrdreCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataSlettCommand;
import no.nav.dolly.config.credentials.PdlDataForvalterProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.service.TokenService;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PdlDataConsumer {

    private final TokenService tokenService;
    private final WebClient webClient;
    private final PdlDataForvalterProperties serviceProperties;

    public PdlDataConsumer(TokenService tokenService, PdlDataForvalterProperties serviceProperties, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(config -> {
                            config.defaultCodecs()
                                    .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                            config.defaultCodecs()
                                    .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                        }).build())
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "pdl_sendOrdre"})
    public String sendOrdre(String ident, boolean isTpsfMaster) {

        return tokenService.generateToken(serviceProperties)
                .flatMap(token -> new PdlDataOrdreCommand(webClient, ident, isTpsfMaster, token.getTokenValue()).call())
                .block();
    }

    @Timed(name = "providers", tags = {"operation", "pdl_delete"})
    public void slettPdl(List<String> identer) {

        String accessToken = serviceProperties.getAccessToken(tokenService);
        identer.stream()
                .map(ident -> Flux.from(new PdlDataSlettCommand(webClient, ident, accessToken).call()))
                .reduce(Flux.empty(), Flux::concat)
                .collectList()
                .block();
    }

    public String opprettPdl(BestillingRequestDTO request) {

        return tokenService.generateToken(serviceProperties)
                .flatMap(token ->
                        new PdlDataOpprettingCommand(webClient, request, token.getTokenValue()).call())
                .block();
    }

    public String oppdaterPdl(String ident, PersonUpdateRequestDTO request) {

        return tokenService.generateToken(serviceProperties)
                .flatMap(token ->
                        new PdlDataOppdateringCommand(webClient, ident, request, token.getTokenValue()).call())
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "pdl_dataforvalter_alive" })
    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }
}
