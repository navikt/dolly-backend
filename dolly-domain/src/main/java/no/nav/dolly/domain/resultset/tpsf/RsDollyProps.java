package no.nav.dolly.domain.resultset.tpsf;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RsDollyProps {

    private String tpsfUrl;
    private String sigrunStubUrl;
    private String krrStubUrl;
    private String kodeverkUrl;
}
