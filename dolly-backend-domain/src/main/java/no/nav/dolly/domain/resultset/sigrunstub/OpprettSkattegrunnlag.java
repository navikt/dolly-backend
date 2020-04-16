package no.nav.dolly.domain.resultset.sigrunstub;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpprettSkattegrunnlag {

    public enum Tjeneste {BEREGNET_SKATT, SUMMERT_SKATTEGRUNNLAG}

    private List<KodeverknavnGrunnlag> grunnlag;
    private String inntektsaar;
    private String personidentifikator;
    private Boolean skjermet;
    private List<KodeverknavnGrunnlag> svalbardGrunnlag;
    private String testdataEier;
    private Tjeneste tjeneste;
}