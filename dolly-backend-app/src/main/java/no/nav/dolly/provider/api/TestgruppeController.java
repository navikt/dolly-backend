package no.nav.dolly.provider.api;

import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;
import static no.nav.dolly.config.CachingConfig.CACHE_TEAM;

import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.service.DollyBestillingService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestillingFraIdenterRequest;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeMedBestillingId;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.TestgruppeService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/gruppe")
public class TestgruppeController {

    private final TestgruppeService testgruppeService;
    private final MapperFacade mapperFacade;
    private final DollyBestillingService dollyBestillingService;
    private final BestillingService bestillingService;

    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @Transactional
    @PutMapping(value = "/{gruppeId}")
    public RsTestgruppeMedBestillingId oppdaterTestgruppe(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsOpprettEndreTestgruppe testgruppe) {
        Testgruppe gruppe = testgruppeService.oppdaterTestgruppe(gruppeId, testgruppe);
        return mapperFacade.map(gruppe, RsTestgruppeMedBestillingId.class);
    }

    @CacheEvict(value = { CACHE_GRUPPE, CACHE_TEAM }, allEntries = true)
    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public RsTestgruppeMedBestillingId opprettTestgruppe(@RequestBody RsOpprettEndreTestgruppe createTestgruppeRequest) {
        Testgruppe gruppe = testgruppeService.opprettTestgruppe(createTestgruppeRequest);
        return mapperFacade.map(testgruppeService.fetchTestgruppeById(gruppe.getId()), RsTestgruppeMedBestillingId.class);
    }

    @Cacheable(CACHE_GRUPPE)
    @GetMapping("/{gruppeId}")
    public RsTestgruppeMedBestillingId getTestgruppe(@PathVariable("gruppeId") Long gruppeId) {
        return mapperFacade.map(testgruppeService.fetchTestgruppeById(gruppeId), RsTestgruppeMedBestillingId.class);
    }

    @Cacheable(CACHE_GRUPPE)
    @GetMapping
    public List<RsTestgruppe> getTestgrupper(
            @RequestParam(name = "brukerId", required = false) String brukerId) {
        return mapperFacade.mapAsList(testgruppeService.getTestgruppeByBrukerId(brukerId), RsTestgruppe.class);
    }

    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @Transactional
    @DeleteMapping("/{gruppeId}")
    public void slettgruppe(@PathVariable("gruppeId") Long gruppeId) {

        testgruppeService.deleteGruppeById(gruppeId);
    }

    @ApiOperation(value = "Opprett berikede testpersoner basert på fødselsdato, kjønn og identtype")
    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{gruppeId}/bestilling")
    public RsBestillingStatus opprettIdentBestilling(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsDollyBestillingRequest request) {
        Bestilling bestilling = bestillingService.saveBestilling(gruppeId, request, request.getTpsf(), request.getAntall(), null, request.getMalBestillingNavn());

        dollyBestillingService.opprettPersonerByKriterierAsync(gruppeId, request, bestilling);
        return mapperFacade.map(bestilling, RsBestillingStatus.class);
    }

    @ApiOperation(value = "Opprett berikede testpersoner basert på eskisterende identer")
    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{gruppeId}/bestilling/fraidenter")
    public RsBestillingStatus opprettIdentBestillingFraIdenter(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsDollyBestillingFraIdenterRequest request) {
        Bestilling bestilling = bestillingService.saveBestilling(gruppeId, request, request.getTpsf(),
                request.getOpprettFraIdenter().size(), request.getOpprettFraIdenter(), request.getMalBestillingNavn());

        dollyBestillingService.opprettPersonerFraIdenterMedKriterierAsync(gruppeId, request, bestilling);
        return mapperFacade.map(bestilling, RsBestillingStatus.class);
    }
}