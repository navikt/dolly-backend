// TODO: Fjern avheningheten til dette bibliotekt som ikke finnes utenfor utv image

package no.nav.tjenester.kodeverk.api.v1;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ApiModel(
        description = "Responsen fra GET /api/v1/kodeverk/{kodeverksnavn}/koder/betydninger."
)
public class GetKodeverkKoderBetydningerResponse {
    @ApiModelProperty(
            value = "Et map med alle eksisterende koder for kodeverket og alle tilhørende betydninger som passer søkekriteriene.",
            required = true
    )
    private Map<String, List<Betydning>> betydninger;

    public GetKodeverkKoderBetydningerResponse(Map<String, List<Betydning>> betydninger) {
        this.setBetydninger(betydninger);
    }

    public void setBetydninger(Map<String, List<Betydning>> betydninger) {
        this.betydninger = new LinkedHashMap(betydninger);
    }

    public Map<String, List<Betydning>> getBetydninger() {
        return this.betydninger;
    }

    public GetKodeverkKoderBetydningerResponse() {
    }
}
