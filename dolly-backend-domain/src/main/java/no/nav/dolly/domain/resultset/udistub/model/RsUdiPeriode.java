package no.nav.dolly.domain.resultset.udistub.model;

import java.time.LocalDateTime;

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
public class RsUdiPeriode {
    private LocalDateTime fra;
    private LocalDateTime til;
}
