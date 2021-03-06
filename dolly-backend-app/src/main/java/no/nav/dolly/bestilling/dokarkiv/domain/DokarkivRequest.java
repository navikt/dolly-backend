package no.nav.dolly.bestilling.dokarkiv.domain;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DokarkivRequest {

    public enum IdType {

        FNR, ORGNR, HPRNR, UTL_ORG
    }

    private String tittel;
    private RsDokarkiv.JournalPostType journalpostType;
    private String tema;
    private String behandlingstema;
    private String kanal;
    private String journalfoerendeEnhet;
    private AvsenderMottaker avsenderMottaker;
    private Bruker bruker;
    private Sak sak;
    private List<Dokument> dokumenter;

    public List<Dokument> getDokumenter() {
        if (isNull(dokumenter)) {
            dokumenter = new ArrayList<>();
            dokumenter.add(new Dokument());
        }
        return dokumenter;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class AvsenderMottaker {

        private String id;
        private IdType idType;
        private String navn;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class Dokument {

        private String tittel;
        private String brevkode;
        private List<DokumentVariant> dokumentvarianter;

        public List<DokumentVariant> getDokumentvarianter() {
            if (isNull(dokumentvarianter)) {
                dokumentvarianter = new ArrayList<>();
                dokumentvarianter.add(new DokumentVariant());
            }
            return dokumentvarianter;
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class DokumentVariant {

        private String filtype;
        private String fysiskDokument;
        private String variantformat;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class Bruker {

        private String id;
        private IdType idType;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class Sak {

        private String fagsakId;
        private String fagsaksystem;
        private String sakstype;
    }
}
