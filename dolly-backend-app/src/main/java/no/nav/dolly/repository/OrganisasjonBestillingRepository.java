package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface OrganisasjonBestillingRepository extends Repository<Bestilling, Long> {

    Optional<OrganisasjonBestilling> findById(Long id);

    OrganisasjonBestilling save(OrganisasjonBestilling bestilling);
}
