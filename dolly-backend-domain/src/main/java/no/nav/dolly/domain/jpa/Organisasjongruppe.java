package no.nav.dolly.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Set;

import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "T_ORGANISASJON_GRUPPE")
public class Organisasjongruppe {

    @Id
    @GeneratedValue(generator = "gruppeIdGenerator")
    @GenericGenerator(name = "gruppeIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "T_GRUPPE_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @Column(nullable = false)
    private String navn;

    @ManyToOne
    @JoinColumn(name = "OPPRETTET_AV", nullable = false)
    private Bruker opprettetAv;

    @ManyToOne
    @JoinColumn(name = "SIST_ENDRET_AV", nullable = false)
    private Bruker sistEndretAv;

    @Column(name = "DATO_ENDRET", nullable = false)
    private LocalDate datoEndret;

    @OrderBy("id")
    @OneToMany(mappedBy = "gruppe", fetch = FetchType.LAZY)
    private Set<OrganisasjonBestilling> bestillinger;

}

