package no.nav.dolly.bestilling.organisasjonforvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BestillingRequest {

    public enum AdresseType {FADR, PADR}

    private List<SyntetiskOrganisasjon> organisasjoner;

    public List<SyntetiskOrganisasjon> getOrganisasjoner() {
        return isNull(organisasjoner) ? new ArrayList<>() : organisasjoner;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class SyntetiskOrganisasjon {

        private String enhetstype;
        private String naeringskode;
        private String sektorkode;
        private String formaal;
        private String telefon;
        private String epost;
        private String nettside;
        private String maalform;

        private List<Adresse> adresser;

        public List<Adresse> getAdresser() {
            return isNull(adresser) ? new ArrayList<>() : adresser;
        }

        private List<SyntetiskOrganisasjon> underenheter;

        public List<SyntetiskOrganisasjon> getUnderenheter() {
            return isNull(underenheter) ? new ArrayList<>() : underenheter;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class Adresse {
            private AdresseType adressetype;
            private List<String> adresselinjer;

            public List<String> getAdresseLinjer() {
                return isNull(adresselinjer) ? new ArrayList<>() : adresselinjer;
            }

            private String postnr;
            private String poststed;
            private String kommunenr;
            private String landkode;
            private String vegadresseId;
        }
    }

}
