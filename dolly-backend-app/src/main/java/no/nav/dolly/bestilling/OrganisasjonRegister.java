package no.nav.dolly.bestilling;

import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;

import java.util.List;

public interface OrganisasjonRegister {

    void gjenopprett(OrganisasjonBestilling bestilling, OrganisasjonBestillingProgress progress, boolean isOpprettEndre);

    void release(List<String> organisasjoner);
}