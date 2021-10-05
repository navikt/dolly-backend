package no.nav.dolly.bestilling.sigrunstub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SigrunResponse {

    private List<ResponseElement> opprettelseTilbakemeldingsListe;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseElement {

        private String personident;
        private Integer status;
        private String message;
    }
}