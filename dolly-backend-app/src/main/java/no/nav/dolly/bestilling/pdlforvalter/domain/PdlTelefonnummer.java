package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdlTelefonnummer {

    private List<Entry> telfonnumre;

    public List<Entry> getTelfonnumre() {
        if (isNull(telfonnumre)) {
            telfonnumre = new ArrayList<>();
        }
        return telfonnumre;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entry extends PdlOpplysning {

        private String landskode;
        private String nummer;
        private Integer prioritet;
    }
}
