package no.nav.dolly.bestilling;

import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;

import java.util.List;

public interface OrganisasjonRegister {

    void opprett(RsOrganisasjonBestilling bestilling, OrganisasjonBestillingProgress progress);

    void gjenoprett(OrganisasjonBestillingProgress progress);

    void release(List<String> organisasjoner);
}