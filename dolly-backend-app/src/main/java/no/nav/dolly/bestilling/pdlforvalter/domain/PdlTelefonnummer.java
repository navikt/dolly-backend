package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PdlTelefonnummer extends PdlOpplysning {

    private List<Entry> telfonnumre;

    public List<Entry> getTelfonnumre() {
        if (isNull(telfonnumre)) {
            telfonnumre = new ArrayList<>();
        }
        return telfonnumre;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entry {

        private String landskode;
        private String nummer;
        private Integer prioritet;
    }
}
