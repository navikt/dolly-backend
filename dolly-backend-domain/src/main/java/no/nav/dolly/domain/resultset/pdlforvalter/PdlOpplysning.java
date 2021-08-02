package no.nav.dolly.domain.resultset.pdlforvalter;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class PdlOpplysning {

    public enum Master {FREG, PDL}

    private String kilde;
    private Master master;
}