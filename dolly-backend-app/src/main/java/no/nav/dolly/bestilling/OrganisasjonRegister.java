package no.nav.dolly.bestilling;

import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;

import java.util.List;

public interface OrganisasjonRegister {

    void gjenopprett(RsOrganisasjonBestilling bestilling, OrganisasjonBestillingProgress progress, boolean isOpprettEndre);

    void release(List<String> organisasjoner);
}