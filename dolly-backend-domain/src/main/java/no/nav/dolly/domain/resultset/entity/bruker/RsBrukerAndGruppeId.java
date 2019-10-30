package no.nav.dolly.domain.resultset.entity.bruker;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class RsBrukerAndGruppeId {

    private String brukerId;
    private List<String> favoritter;

    public List<String> getFavoritter() {
        if (isNull(favoritter)) {
            favoritter = new ArrayList();
        }
        return favoritter;
    }
}