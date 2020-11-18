package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface OrganisasjonBestillingProgressRepository extends Repository<BestillingProgress, Long> {

    Optional<OrganisasjonBestillingProgress> save(OrganisasjonBestillingProgress bestillingProgress);

    Optional<List<OrganisasjonBestillingProgress>> findByBestillingId(Long bestillingId);
}
