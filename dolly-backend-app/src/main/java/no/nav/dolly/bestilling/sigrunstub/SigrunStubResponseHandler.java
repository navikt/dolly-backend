package no.nav.dolly.bestilling.sigrunstub;

import no.nav.dolly.bestilling.sigrunstub.dto.SigrunResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class SigrunStubResponseHandler {

    public String extractResponse(ResponseEntity<SigrunResponse> response) {

        return nonNull(response) && response.getBody().getOpprettelseTilbakemeldingsListe().stream()
                .noneMatch(element -> 200 != element.getStatus()) ? "OK" :
                response.getBody().getOpprettelseTilbakemeldingsListe().stream()
                        .filter(element -> 200 != element.getStatus())
                        .map(element -> "FEIL: " + element.getMessage())
                        .findFirst().get();
    }
}