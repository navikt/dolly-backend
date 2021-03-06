package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlUtflytting {

    private Folkeregistermetadata folkeregistermetadata;
    private String kilde;
    private String tilflyttingsland;
    private String tilflyttingsstedIUtlandet;
}