package no.nav.dolly.bestilling.pdldata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PdlDataOrdreCommand implements Callable<Mono<String>> {

    private static final String PDL_FORVALTER_ORDRE_URL = "/api/v1/personer";
    private static final String PDL_FORVALTER_ORDRE_URL = "/api/v1/personer/{ident}/ordre";
    private static final String IS_TPS_MASTER = "isTpsMaster";

    private final WebClient webClient;
    private final String ident;
    private final String token; //TODO FIX

    public Mono<String> call() {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_FORVALTER_ORDRE_URL)
                        .queryParam(IS_TPS_MASTER, true)
                        .pathSegment(identer.getIdenter().get(0))
                        .pathSegment("ordre")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(identer)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }
}
