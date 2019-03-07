package no.nav.dolly.domain.resultset.aareg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsAktoerPerson extends RsAktoer {

    private String ident;
    private String identtype;
}
