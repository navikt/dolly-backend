package no.nav.dolly.domain.resultset.aareg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsAmeldingRequest {

    private LocalDate maaned;
    private List<RsArbeidsforhold> rsArbeidsforhold;
}
