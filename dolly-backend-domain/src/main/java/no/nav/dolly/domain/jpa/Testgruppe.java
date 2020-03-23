package no.nav.dolly.domain.jpa;

import static java.util.Objects.isNull;
import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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
@Table(name = "T_GRUPPE")
public class Testgruppe {

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

    @Column
    private String hensikt;

    @ManyToOne
    @JoinColumn(name = "OPPRETTET_AV", nullable = false)
    private Bruker opprettetAv;

    @ManyToOne
    @JoinColumn(name = "SIST_ENDRET_AV", nullable = false)
    private Bruker sistEndretAv;

    @Column(name = "DATO_ENDRET", nullable = false)
    private LocalDate datoEndret;

    @OneToMany(mappedBy = "testgruppe", fetch = FetchType.LAZY)
    @Column(unique = true)
    private Set<Testident> testidenter;

    @ManyToMany(mappedBy = "favoritter", fetch = FetchType.LAZY)
    private Set<Bruker> favorisertAv;

    @OrderBy("id")
    @OneToMany(mappedBy = "gruppe", fetch = FetchType.LAZY)
    private Set<Bestilling> bestillinger;

    @Column(name = "ER_LAAST")
    private Boolean erLaast;

    @Column(name = "LAAST_BESKRIVELSE")
    private String laastBeskrivelse;

    public Set<Testident> getTestidenter() {
        if (isNull(testidenter)) {
            testidenter = new HashSet();
        }
        return testidenter;
    }

    public Set<Bruker> getFavorisertAv() {
        if (isNull(favorisertAv)) {
            favorisertAv = new HashSet();
        }
        return favorisertAv;
    }
}

