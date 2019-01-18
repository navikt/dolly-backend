package no.nav.dolly.domain.resultset;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Medlemsvariabelen status er (value) lagringsstatus for skdmeldingen per (key) miljø.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendSkdMeldingTilTpsResponse {
    private String personId;
    private String skdmeldingstype;
    private Map<String, String> status; //Map<Environment, TPS respons statusmelding >
}
