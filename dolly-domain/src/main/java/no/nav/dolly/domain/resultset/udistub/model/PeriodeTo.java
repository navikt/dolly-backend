package no.nav.dolly.domain.resultset.udistub.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PeriodeTo {
    private LocalDate fra;
    private LocalDate til;
}
