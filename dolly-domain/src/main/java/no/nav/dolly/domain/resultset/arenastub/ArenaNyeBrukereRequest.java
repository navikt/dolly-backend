package no.nav.dolly.domain.resultset.arenastub;

import java.util.List;

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
public class ArenaNyeBrukereRequest {

    private List<RsArenadata> nyeBrukere;
}
