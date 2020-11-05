package no.nav.dolly.repository.oracle;

import org.springframework.data.repository.PagingAndSortingRepository;

import no.nav.dolly.domain.jpa.oracle.OraBestilling;

public interface OraBestillingRepository extends PagingAndSortingRepository<OraBestilling, Long> {

}
