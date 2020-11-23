package no.nav.dolly.repository.postgres;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import no.nav.dolly.domain.jpa.postgres.Testgruppe;

public interface TestgruppeRepository extends PagingAndSortingRepository<Testgruppe, Long> {

    Optional<Testgruppe> findById(Long id);

    List<Testgruppe> findAllByOrderById();

    List<Testgruppe> findAllById(Iterable<Long> ids);

    Testgruppe save(Testgruppe testgruppe);

    Set<Testgruppe> findAllByOrderByNavn();

    Page<Testgruppe> findAllByOrderByNavn(Pageable pageable);

    int deleteTestgruppeById(Long id);
}
