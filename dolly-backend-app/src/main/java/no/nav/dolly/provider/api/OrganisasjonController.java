package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.OrganisasjonRegister;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonBestillingStatus;
import no.nav.dolly.repository.OrganisasjonBestillingProgressRepository;
import no.nav.dolly.service.OrganisasjonBestillingService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

import static no.nav.dolly.config.CachingConfig.CACHE_ORG_BESTILLING;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/organisasjon")
public class OrganisasjonController {

    private final OrganisasjonRegister organisasjonClient;
    private final OrganisasjonBestillingService bestillingService;
    private final OrganisasjonBestillingProgressRepository bestillingProgressRepository;
    private final MapperFacade mapperFacade;

    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(value = CACHE_ORG_BESTILLING, allEntries = true)
    @PostMapping("/bestilling")
    @Operation(description = "Opprett organisasjon")
    public RsOrganisasjonBestillingStatus opprettOrganisasjonBestilling(@RequestBody RsOrganisasjonBestilling request) {

        OrganisasjonBestilling bestilling = bestillingService.saveBestilling(request);
        organisasjonClient.opprett(request, bestilling.getId());

        return bestillingService.fetchBestillingStatusById(bestilling.getId());
    }

    @CacheEvict(value = CACHE_ORG_BESTILLING, allEntries = true)
    @PutMapping("/bestilling")
    @Operation(description = "Gjenopprett organisasjon")
    public void gjenopprettOrganisasjon(@RequestParam Long bestillingId, @RequestParam List<String> miljoer) {

        Optional<List<OrganisasjonBestillingProgress>> bestillingProgress = bestillingProgressRepository.findByBestillingId(bestillingId);

        if (bestillingProgress.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Fant ikke noen bestillinger med ID: " + bestillingId);
        }

        bestillingProgress.ifPresent(progressList ->
                progressList.forEach(progress -> organisasjonClient.gjenopprett(progress, miljoer)));
    }

    @GetMapping("/bestilling")
    @Cacheable(value = CACHE_ORG_BESTILLING)
    @Operation(description = "Hent status på bestilling basert på bestillingId")
    public RsOrganisasjonBestillingStatus hentBestilling(
            @Parameter(description = "ID på bestilling av organisasjon", example = "123") @RequestParam Long bestillingId) {

        return bestillingService.fetchBestillingStatusById(bestillingId);
    }

    @GetMapping("/bestillingsstatus")
    @Cacheable(value = CACHE_ORG_BESTILLING)
    @Operation(description = "Hent status på bestilling basert på brukerId")
    public List<RsOrganisasjonBestillingStatus> hentBestillingStatus(
            @Parameter(description = "BrukerID som er unik til en Azure bruker (Dolly autensiering)", example = "1k9242uc-638g-1234-5678-7894k0j7lu6n") @RequestParam String brukerId) {

        return bestillingService.fetchBestillingStatusByBrukerId(brukerId);
    }
}