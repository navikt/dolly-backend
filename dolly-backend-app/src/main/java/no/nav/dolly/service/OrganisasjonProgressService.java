package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.OrganisasjonBestillingProgressRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganisasjonProgressService {

    private final OrganisasjonBestillingProgressRepository organisasjonProgressRepository;

    public Optional<OrganisasjonBestillingProgress> save(OrganisasjonBestillingProgress progress) {

        return organisasjonProgressRepository.save(progress);
    }

    public List<OrganisasjonBestillingProgress> fetchOrganisasjonBestillingProgressByBestillingsIdFromDB(Long bestillingsId) {
        return organisasjonProgressRepository.findByBestillingId(bestillingsId).orElseThrow(
                () -> new NotFoundException("Kunne ikke finne bestillingsprogress med bestillingId=" + bestillingsId + ", i tabell T_ORGANISASJON_BESTILLINGS_PROGRESS"));
    }
}