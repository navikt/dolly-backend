package no.nav.dolly.repository.oracle;

import org.springframework.data.repository.PagingAndSortingRepository;

import no.nav.dolly.domain.jpa.oracle.OraTransaksjonMapping;

public interface OraTransaksjonMappingRepository extends PagingAndSortingRepository<OraTransaksjonMapping, Long> {

}