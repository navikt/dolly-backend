package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonBestillingStatus;
import no.nav.dolly.service.OrganisasjonBestillingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/organisasjon")
public class OrganisasjonController {

    private final OrganisasjonBestillingService bestillingService;
    private final MapperFacade mapperFacade;

    @Operation(description = "Legge til/Endre egenskaper på Organisasjon")
    @PutMapping("/{organisasjon}/leggTilPaaOrganisasjon")
    @ResponseStatus(HttpStatus.OK)
    public RsOrganisasjonBestillingStatus endreOrganisasjon(@PathVariable Integer organisasjon, @RequestBody RsOrganisasjonBestilling request) {

        OrganisasjonBestilling bestilling = bestillingService.saveBestilling(request, organisasjon);
        return mapperFacade.map(bestilling, RsOrganisasjonBestillingStatus.class);
    }

}