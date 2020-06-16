package no.nav.dolly.provider.api;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.service.DollyBestillingService;
import no.nav.dolly.domain.MalbestillingNavn;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.MalBestillingService;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/bestilling", produces = MediaType.APPLICATION_JSON_VALUE)
public class BestillingController {

    private final MapperFacade mapperFacade;
    private final BestillingService bestillingService;
    private final MalBestillingService malBestillingService;
    private final DollyBestillingService dollyBestillingService;

    @Cacheable(value = CACHE_BESTILLING)
    @GetMapping("/{bestillingId}")
    @ApiOperation(value = "Hent Bestilling med bestillingsId", authorizations = { @Authorization(value = "Bearer token fra bruker") })
    public RsBestillingStatus getBestillingById(@PathVariable("bestillingId") Long bestillingId) {
        return mapperFacade.map(bestillingService.fetchBestillingById(bestillingId), RsBestillingStatus.class);
    }

    @Cacheable(value = CACHE_BESTILLING)
    @GetMapping("/gruppe/{gruppeId}")
    @ApiOperation(value = "Hent Bestillinger tilhørende en gruppe med gruppeId", authorizations = { @Authorization(value = "Bearer token fra bruker") })
    public List<RsBestillingStatus> getBestillinger(@PathVariable("gruppeId") Long gruppeId) {
        return mapperFacade.mapAsList(bestillingService.fetchBestillingerByGruppeId(gruppeId), RsBestillingStatus.class);
    }

    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @DeleteMapping("/stop/{bestillingId}")
    @ApiOperation(value = "Stopp en Bestilling med bestillingsId", authorizations = { @Authorization(value = "Bearer token fra bruker") })
    public RsBestillingStatus stopBestillingProgress(@PathVariable("bestillingId") Long bestillingId) {
        Bestilling bestilling = bestillingService.cancelBestilling(bestillingId);
        return mapperFacade.map(bestilling, RsBestillingStatus.class);
    }

    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @PostMapping("/gjenopprett/{bestillingId}")
    @ApiOperation(value = "Gjenopprett en bestilling med bestillingsId, for en liste med miljoer", authorizations = { @Authorization(value = "Bearer token fra bruker") })
    public RsBestillingStatus gjenopprettBestilling(@PathVariable("bestillingId") Long bestillingId, @RequestParam(value = "miljoer", required = false) String miljoer) {
        Bestilling bestilling = bestillingService.createBestillingForGjenopprett(bestillingId, nonNull(miljoer) ? asList(miljoer.split(",")) : emptyList());
        dollyBestillingService.gjenopprettBestillingAsync(bestilling);
        return mapperFacade.map(bestilling, RsBestillingStatus.class);
    }

    @Cacheable(value = CACHE_BESTILLING)
    @GetMapping("/malbestilling")
    @ApiOperation(value = "Hent mal-bestilling", authorizations = { @Authorization(value = "Bearer token fra bruker") })
    public RsMalBestillingWrapper getMalBestillinger() {

        return malBestillingService.getMalBestillinger();
    }

    @DeleteMapping("/malbestilling/{id}")
    @ApiOperation(value = "Slett mal-bestilling", authorizations = { @Authorization(value = "Bearer token fra bruker") })
    public void deleteMalBestilling(@PathVariable Long id) {

        bestillingService.redigerBestilling(id, null);
    }

    @PutMapping("/malbestilling/{id}")
    @ApiOperation(value = "Rediger mal-bestilling", authorizations = { @Authorization(value = "Bearer token fra bruker") })
    public void redigerMalBestilling(@PathVariable Long id, @RequestBody MalbestillingNavn malbestillingNavn) {

        bestillingService.redigerBestilling(id, malbestillingNavn.getMalNavn());
    }
}