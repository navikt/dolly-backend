package no.nav.jpa;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_BRUKER")
public class Bruker {

    @Id
    @Column(name = "NAV_IDENT", length = 10)
    private String navIdent;

    @ManyToMany(mappedBy = "brukere")
    @Column(name = "TEAM_MEDLEMSKAP")
    private Set<Team> teamMedlemskap;
    
    @OneToMany(mappedBy = "eier")
    @Column(name = "TEAM_EIERSKAP",unique = true)
    private Set<Team> teamEierskap;
}
