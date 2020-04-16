package no.nav.dolly.domain.resultset.tpsf;

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
public class RsBarnRelasjonRequest {

    public enum BorHos {MEG, OSS, DEG}

    @ApiModelProperty(
            position = 1,
            required = true,
            value= "Ident for barnet"
    )
    private String ident;

    @ApiModelProperty(
            position = 2,
            value= "Ident som identifiserer partner for felles eller dine barn. Kan være tom for mine barn eller når det finnes kun en partner"
    )
    private String partnerIdent;

    @ApiModelProperty(
            position = 3,
            value= "Barns boadresse bestemmes ut fra attributtverdi, og blank, MEG og OSS gir boadresse identisk med hovedperson"
    )
    private BorHos borHos;
}