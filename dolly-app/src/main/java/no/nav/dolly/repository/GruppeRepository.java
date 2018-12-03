package no.nav.dolly.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;

public interface GruppeRepository extends JpaRepository<Testgruppe, Long> {

	Testgruppe save(Testgruppe testgruppe);

	List<Testgruppe> findAll();

	List<Testgruppe> findAllByTeamtilhoerighet(Team team);

	Testgruppe findByNavn(String navn);

	void deleteTestgruppeById(Long id);
}
