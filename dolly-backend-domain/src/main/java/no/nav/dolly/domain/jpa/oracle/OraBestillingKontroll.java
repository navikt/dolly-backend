package no.nav.dolly.domain.jpa.oracle;

import static no.nav.dolly.domain.jpa.postgres.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_BESTILLING_KONTROLL")
public class OraBestillingKontroll {

    @Id
    @GeneratedValue(generator = "oraBestillingKontrollIdGenerator")
    @GenericGenerator(name = "oraBestillingKontrollIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "T_BESTILLING_KONTROLL_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @JoinColumn(name = "BESTILLING_ID", nullable = false)
    private Long bestillingId;

    @Column(name = "STOPPET", nullable = false)
    private boolean stoppet;
}
