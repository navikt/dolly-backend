package no.nav.dolly.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_ORGANISASJON_TRANSAKSJON_MAPPING")
public class OrganisasjonTransaksjonMapping {

    @Id
    @GeneratedValue(generator = "transaksjonMappingIdGenerator")
    @GenericGenerator(name = "transaksjonMappingIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "T_TRANSAKSJON_MAPPING_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @Column(name = "BESTILLING_ID")
    private Long bestillingId;

    @Column(name = "ORGANISASJON")
    private String ident;

    @Column(name = "SYSTEM")
    private String system;

    @Column(name = "MILJOE")
    private String miljoe;

    @Column(name = "TRANSAKSJON_ID")
    private String transaksjonId;

    @Column(name = "DATO_ENDRET")
    private LocalDateTime datoEndret;
}
