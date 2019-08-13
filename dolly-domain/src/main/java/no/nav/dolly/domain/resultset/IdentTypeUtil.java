package no.nav.dolly.domain.resultset;

import static no.nav.dolly.domain.resultset.IdentType.BOST;
import static no.nav.dolly.domain.resultset.IdentType.DNR;
import static no.nav.dolly.domain.resultset.IdentType.FDAT;
import static no.nav.dolly.domain.resultset.IdentType.FNR;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdentTypeUtil {

    public static IdentType getIdentType(String ident) {

        if (Character.getType(ident.charAt(0)) > 3) {
            return DNR;

        } else if (Character.getType(ident.charAt(3)) > 2) {
            return BOST;

        } else if ("00000".equals(ident.substring(6))) {
            return FDAT;

        } else {
            return FNR;
        }
    }
}
