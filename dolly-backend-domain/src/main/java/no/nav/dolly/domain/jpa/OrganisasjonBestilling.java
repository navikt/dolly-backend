package no.nav.dolly.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ORGANISASJON_BESTILLING")
public class OrganisasjonBestilling {

    @Id
    @GeneratedValue(generator = "bestillingIdGenerator")
    @GenericGenerator(name = "bestillingIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "T_BESTILLING_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @Column(name = "MILJOER", nullable = false)
    private String miljoer;

    @Column(name = "ANTALL", nullable = false)
    private Integer antall;

    @Column(name = "SIST_OPPDATERT", nullable = false)
    @UpdateTimestamp
    private LocalDateTime sistOppdatert;

    @Column(name = "FEIL")
    private String feil;

    @Column(name = "OPPRETTET_FRA_ID")
    private Long opprettetFraId;

    @Column(name = "BEST_KRITERIER")
    private String bestKriterier;

    @ManyToOne
    @JoinColumn(name = "BRUKER_ID", nullable = false)
    private Bruker bruker;

    @OneToMany(mappedBy = "bestillingId", fetch = FetchType.LAZY)
    private List<OrganisasjonBestillingProgress> progresser;

    public List<OrganisasjonBestillingProgress> getProgresser() {
        if (isNull(progresser)) {
            progresser = new ArrayList<>();
        }
        return progresser;
    }
}