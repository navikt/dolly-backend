package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonBestillingStatus;
import no.nav.dolly.service.OrganisasjonBestillingService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/organisasjon")
public class OrganisasjonController {

    private final OrganisasjonBestillingService bestillingService;
    private final MapperFacade mapperFacade;


    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/bestilling")
    @Operation(description = "Opprett organisasjon")
    public RsOrganisasjonBestillingStatus opprettIdentBestilling(@RequestBody RsOrganisasjonBestilling request) {
        OrganisasjonBestilling bestilling = bestillingService.saveBestilling(request, request.getAntall());

        return mapperFacade.map(bestilling, RsOrganisasjonBestillingStatus.class);
    }

    @Operation(description = "Legge til/Endre egenskaper på Organisasjon")
    @PutMapping("/{organisasjon}/leggTilPaaOrganisasjon")
    @ResponseStatus(HttpStatus.OK)
    public RsOrganisasjonBestillingStatus endreOrganisasjon(@PathVariable Integer organisasjon, @RequestBody RsOrganisasjonBestilling request) {

        OrganisasjonBestilling bestilling = bestillingService.saveBestilling(request, organisasjon);
        return mapperFacade.map(bestilling, RsOrganisasjonBestillingStatus.class);
    }

}