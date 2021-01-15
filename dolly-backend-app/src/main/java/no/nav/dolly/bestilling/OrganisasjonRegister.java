package no.nav.dolly.bestilling;

import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployResponse;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public interface OrganisasjonRegister {

    void opprett(RsOrganisasjonBestilling bestilling, Long bestillingId);

    ResponseEntity<DeployResponse> gjenopprett(Set<String> orgnumre, List<String> miljoer);

    void release(List<String> organisasjoner);
}