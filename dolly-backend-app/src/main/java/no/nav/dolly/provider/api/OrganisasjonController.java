package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.organisasjonforvalter.OrganisasjonClient;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonBestillingStatus;
import no.nav.dolly.repository.OrganisasjonBestillingProgressRepository;
import no.nav.dolly.service.OrganisasjonBestillingService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/organisasjon")
public class OrganisasjonController {

    private final OrganisasjonClient organisasjonClient;
    private final OrganisasjonBestillingService bestillingService;
    private final OrganisasjonBestillingProgressRepository bestillingProgressRepository;
    private final MapperFacade mapperFacade;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/bestilling")
    @Operation(description = "Opprett organisasjon")
    public RsOrganisasjonBestillingStatus opprettOrganisasjonBestilling(@RequestBody RsOrganisasjonBestilling request) {

        OrganisasjonBestilling bestilling = bestillingService.saveBestilling(request);
        organisasjonClient.opprett(request, bestilling.getId());

        return mapperFacade.map(bestilling, RsOrganisasjonBestillingStatus.class);
    }

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
    @Operation(description = "Hent status på bestilling basert på bestillingId eller brukerId. En av de må velges")
    public List<OrganisasjonBestillingProgress> hentStatus(
            @Parameter(description = "ID på bestilling av organisasjon", example = "123") @RequestParam(required = false) Long bestillingId,
            @Parameter(description = "BrukerID som er unik til en Azure bruker (Dolly autensiering)", example = "1k9242uc-638g-1234-5678-7894k0j7lu6n") @RequestParam(required = false) String brukerId) {

        if (isNull(bestillingId) && isNull(brukerId)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Kallet må inneholde enten BestillingId eller BrukerID");
        }

        Optional<List<OrganisasjonBestillingProgress>> bestillingProgress = nonNull(brukerId)
                ? bestillingProgressRepository.findbyBrukerId(brukerId)
                : bestillingProgressRepository.findByBestillingId(bestillingId);

        if (bestillingProgress.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, nonNull(brukerId)
                    ? "Fant ikke noen bestillinger med brukerId: " + brukerId
                    : "Fant ikke noen bestillinger med ID: " + bestillingId);
        }
        List<OrganisasjonBestillingProgress> statusList = new ArrayList<>();

        bestillingProgress.ifPresent(statusList::addAll);

        return statusList;
    }
}