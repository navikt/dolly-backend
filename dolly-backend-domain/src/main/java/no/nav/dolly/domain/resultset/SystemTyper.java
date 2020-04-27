package no.nav.dolly.domain.resultset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public enum SystemTyper {

    AAREG("Arbeidsregister (AAREG)"),
    TPSF("Tjenestebasert personsystem (TPS)"),
    INST2("Institusjonsopphold (INST2)"),
    KRRSTUB("Digital kontaktinformasjon (DKIF)"),
    SIGRUNSTUB("Skatteinntekt grunnlag (SIGRUN)"),
    ARENA("Arena fagsystem"),
    PDL_FORVALTER("Persondataløsningen (PDL)"),
    PDL_FALSKID("Falsk identitet (PDL)"),
    PDL_DODSBO("Kontaktinformasjon dødsbo (PDL)"),
    PDL_UTENLANDSID("Utenlandsk id (PDL)"),
    UDISTUB("Utlendingsdirektoratet (UDI)"),
    INNTK("Inntektskomponenten (INNTK)"),
    PEN_FORVALTER("Pensjon (PEN)"),
    PEN_INNTEKT("Pensjonsopptjening (POPP)"),
    INNTKMELD("Inntektsmelding (ALTINN/JOARK)"),
    BREGSTUB("Brønnøysundregistrene (BREGSTUB)");

    SystemTyper(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    private String beskrivelse;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemBeskrivelse {

        private String system;
        private String beskrivelse;
    }
}
