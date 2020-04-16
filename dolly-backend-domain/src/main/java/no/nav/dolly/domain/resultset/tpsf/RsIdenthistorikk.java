package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsIdenthistorikk {

    @ApiModelProperty(
            position = 1,
            value = "Identtype FNR/DNR/BOST, default er FNR"
    )
    private String identtype;

    @ApiModelProperty(
            position = 2,
            value = "Født etter denne dato. Dafault velges voksne personer i alder 30-60, mens barn får alder 0-18"
    )
    private LocalDateTime foedtEtter;

    @ApiModelProperty(
            position = 4,
            value = "Født før denne dato. Dafault velges voksne personer i alder 30-60, mens barn får alder 0-18"
    )
    private LocalDateTime foedtFoer;

    @ApiModelProperty(
            position = 5,
            value = "Kjønn på testperson. Gyldige verdier: 'K', 'M' og 'U'. Ubestemt betyr at systemet velger for deg og generert person blir mann eller kvinne"
    )
    private String kjonn;

    @ApiModelProperty(
            position = 6,
            value = "Registreringsdato for endring av denne ident")
    private LocalDateTime regdato;
}