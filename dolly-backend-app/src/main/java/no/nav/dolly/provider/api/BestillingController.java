package no.nav.dolly.provider.api;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.service.DollyBestillingService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;
import no.nav.dolly.service.BestillingService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/bestilling", produces = MediaType.APPLICATION_JSON_VALUE)
public class BestillingController {

    private final MapperFacade mapperFacade;
    private final BestillingService bestillingService;
    private final DollyBestillingService dollyBestillingService;

    @Cacheable(value = CACHE_BESTILLING)
    @GetMapping("/{bestillingId}")
    @ApiOperation("Hent Bestilling med bestillingsId")
    public RsBestillingStatus getBestillingById(@PathVariable("bestillingId") Long bestillingId) {
        return mapperFacade.map(bestillingService.fetchBestillingById(bestillingId), RsBestillingStatus.class);
    }

    @Cacheable(value = CACHE_BESTILLING)
    @GetMapping("/{bestillingId}/ny")
    @ApiOperation("Hent bestillingsStatus med bestillingsId")
    public RsBestillingStatus getBestillingsstatus(@PathVariable("bestillingId") Long bestillingId) {
        return mapperFacade.map(bestillingService.fetchBestillingById(bestillingId), RsBestillingStatus.class);
    }

    @Cacheable(value = CACHE_BESTILLING)
    @GetMapping("/gruppe/{gruppeId}")
    @ApiOperation("Hent Bestillinger tilhørende en gruppe med gruppeId")
    public List<RsBestillingStatus> getBestillinger(@PathVariable("gruppeId") Long gruppeId) {
        return mapperFacade.mapAsList(bestillingService.fetchBestillingerByGruppeId(gruppeId), RsBestillingStatus.class);
    }

    @Cacheable(value = CACHE_BESTILLING)
    @GetMapping("/gruppe/{gruppeId}/ny")
    @ApiOperation("Hent status på Bestillinger tilhørende en gruppe med gruppeId")
    public List<RsBestillingStatus> getStatusForBestillinger(@PathVariable("gruppeId") Long gruppeId) {
        return mapperFacade.mapAsList(bestillingService.fetchBestillingerByGruppeId(gruppeId), RsBestillingStatus.class);
    }

    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @DeleteMapping("/stop/{bestillingId}")
    @ApiOperation("Stopp en Bestilling med bestillingsId")
    public RsBestilling stopBestillingProgress(@PathVariable("bestillingId") Long bestillingId) {
        Bestilling bestilling = bestillingService.cancelBestilling(bestillingId);
        return mapperFacade.map(bestilling, RsBestilling.class);
    }

    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @PostMapping("/gjenopprett/{bestillingId}")
    @ApiOperation("Gjenopprett en bestilling med bestillingsId, for en liste med miljoer")
    public RsBestilling gjenopprettBestilling(@PathVariable("bestillingId") Long bestillingId, @RequestParam(value = "miljoer", required = false) String miljoer) {
        Bestilling bestilling = bestillingService.createBestillingForGjenopprett(bestillingId, nonNull(miljoer) ? asList(miljoer.split(",")) : emptyList());
        dollyBestillingService.gjenopprettBestillingAsync(bestilling);
        return mapperFacade.map(bestilling, RsBestilling.class);
    }

    @GetMapping("/malbestilling")
    @ApiOperation("Hent mal-bestilling")
    public List<RsBestilling> getMalBestillinger() {
        return mapperFacade.mapAsList(bestillingService.fetchMalBestillinger(), RsBestilling.class);
    }
}