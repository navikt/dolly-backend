package no.nav.dolly.bestilling.pdldata;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pdldata.command.PdlDataOppdateringCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataOpprettingCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataOrdreCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataSlettCommand;
import no.nav.dolly.config.credentials.PdlDataForvalterProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.service.TokenService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Service
public class PdlDataConsumer {

    private final TokenService tokenService;
    private final WebClient webClient;
    private final PdlDataForvalterProperties properties;

    public PdlDataConsumer(TokenService tokenService, PdlDataForvalterProperties serviceProperties) {
        this.tokenService = tokenService;
        this.properties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "pdl_sendOrdre"})
    public String sendOrdre(String ident, boolean isTpsfMaster) {

        return tokenService.generateToken(properties)
                .flatMap(token -> new PdlDataOrdreCommand(webClient, ident, isTpsfMaster, token.getTokenValue()).call())
                .block();
    }

    @Timed(name = "providers", tags = {"operation", "pdl_delete"})
    public void slettPdl(List<String> identer) {

        tokenService.generateToken(properties)
                .flatMapMany(token -> identer.stream()
                        .map(ident -> Flux.from(new PdlDataSlettCommand(webClient, ident, token.getTokenValue()).call()))
                        .reduce(Flux.empty(), Flux::concat))
                .collectList()
                .block();
    }

    public String opprettPdl(BestillingRequestDTO request) {

        return tokenService.generateToken(properties)
                .flatMap(token ->
                        new PdlDataOpprettingCommand(webClient, request, token.getTokenValue()).call())
                .block();
    }

    public String oppdaterPdl(String ident, PersonUpdateRequestDTO request) {

        return tokenService.generateToken(properties)
                .flatMap(token ->
                        new PdlDataOppdateringCommand(webClient, ident, request, token.getTokenValue()).call())
                .block();
    }
}
