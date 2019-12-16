package no.nav.dolly.domain.resultset.udistub.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.nav.dolly.domain.resultset.udistub.model.arbeidsadgang.RsUdiArbeidsadgang;
import no.nav.dolly.domain.resultset.udistub.model.avgjoerelse.RsUdiAvgjorelse;
import no.nav.dolly.domain.resultset.udistub.model.opphold.RsUdiOppholdStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RsUdiPerson {

        private List<RsUdiAvgjorelse> avgjoerelser;
        private List<RsUdiAlias> aliaser;
        private RsUdiArbeidsadgang arbeidsadgang;
        private RsUdiOppholdStatus oppholdStatus;
        private Boolean avgjoerelseUavklart;
        private Boolean harOppholdsTillatelse;
        private Boolean flyktning;
        private String soeknadOmBeskyttelseUnderBehandling;
        private LocalDateTime soknadDato;
}
