package no.nav.dolly.domain.resultset.arenaforvalter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArenaNyeDagpenger {

    private List<ArenaDagpenger> nyeDagp;

    public List<ArenaDagpenger> getNyeDagp() {
        if (isNull(nyeDagp)) {
            nyeDagp = new ArrayList<>();
        }
        return nyeDagp;
    }
}
