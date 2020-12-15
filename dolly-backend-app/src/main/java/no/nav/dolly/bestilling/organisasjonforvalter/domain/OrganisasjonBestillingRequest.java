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

    public List<Organisasjon> getOrganisasjoner() {
        return isNull(organisasjoner) ? new ArrayList<>() : organisasjoner;
    }

    private List<Organisasjon> organisasjoner;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Organisasjon {

        private String id;
        private String parentId;
        private String organisasjonsform;
        private String naeringskode;
        private String formaal;
        private String telefon;
        private String epost;
        private String nettside;
        private ForretningsAdresse forretningsAdresse;
        private List<Organisasjon> underenheter;
    }

    public static class ForretningsAdresse {
        private String adresseType;
        private String adresseLinje1;
        private String adresseLinje2;
        private String adresseLinje3;
        private String adresseLinje4;
        private String adresseLinje5;
        private String postnr;
        private String kommunenr;
        private String landkode;
    }
}
