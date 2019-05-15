package no.nav.dolly.domain.resultset.arenaforvalter;

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
public class ArenaBrukereUtenServicebehov implements Arenadata {

    private List<RsArenadata> nyeBrukereUtenServiceBehov;
}
