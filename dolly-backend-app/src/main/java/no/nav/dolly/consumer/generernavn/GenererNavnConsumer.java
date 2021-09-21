package no.nav.dolly.consumer.generernavn;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class GenererNavnConsumer {

    private static final String FIKTIVE_NAVN_URL = "/api/v1/navn";

    private final WebClient webClient;

    public GenererNavnConsumer(ProvidersProps providersProps) {
        this.webClient = WebClient.builder()
                .baseUrl(providersProps.getGenererNavnService().getUrl()).build();
    }

    public ResponseEntity<JsonNode> getPersonnavn(Integer antall) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(FIKTIVE_NAVN_URL)
                        .queryParam("antall", antall)
                        .build())
                .retrieve()
                .toEntity(JsonNode.class).block();
    }
}
