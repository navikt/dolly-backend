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
public class DeployRequest {

    private List<String> orgnumre;
    private List<String> environments;

    public List<String> getOrgnumre() {
        return isNull(orgnumre) ? new ArrayList<>() : orgnumre;
    }

    public List<String> getEnvironments() {
        return isNull(environments) ? new ArrayList<>() : environments;
    }
}