package no.nav.dolly.bestilling.organisasjonforvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganisasjonBestillingRequest {

    private List<SyntetiskOrganisasjon> organisasjoner;

    public List<SyntetiskOrganisasjon> getOrganisasjoner() {
        return isNull(organisasjoner) ? new ArrayList<>() : organisasjoner;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class SyntetiskOrganisasjon {

        private String enhetstype;
        private String organisasjonsform;
        private String naeringskode;
        private String formaal;
        private String telefon;
        private String epost;
        private String nettside;

        private List<Adresse> adresser;

        public List<Adresse> getAdresser() {
            return isNull(adresser) ? new ArrayList<>() : adresser;
        }

        private List<SyntetiskOrganisasjon> underenheter;

        public List<SyntetiskOrganisasjon> getUnderenheter() {
            return isNull(underenheter) ? new ArrayList<>() : underenheter;
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Adresse {
        private String adresseType;
        private List<String> adresseLinjer;

        public List<String> getAdresseLinjer() {
            return isNull(adresseLinjer) ? new ArrayList<>() : adresseLinjer;
        }

        private String postnr;
        private String kommunenr;
        private String landkode;
        private String gatekode;
        private String boenhet;
    }
}
