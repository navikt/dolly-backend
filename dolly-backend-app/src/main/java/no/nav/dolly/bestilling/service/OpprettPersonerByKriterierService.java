package no.nav.dolly.bestilling.service;

import static java.util.Objects.nonNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.tpsf.TpsfResponseHandler;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TpsfPersonCache;

@Slf4j
@Service
public class OpprettPersonerByKriterierService extends DollyBestillingService {

    private BestillingService bestillingService;
    private ErrorStatusDecoder errorStatusDecoder;
    private MapperFacade mapperFacade;
    private TpsfService tpsfService;
    private ForkJoinPool dollyForkJoinPool;

    public OpprettPersonerByKriterierService(TpsfResponseHandler tpsfResponseHandler, TpsfService tpsfService,
            TpsfPersonCache tpsfPersonCache, IdentService identService, BestillingProgressRepository bestillingProgressRepository,
            BestillingService bestillingService, MapperFacade mapperFacade, CacheManager cacheManager,
            ObjectMapper objectMapper, List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
            ErrorStatusDecoder errorStatusDecoder, ForkJoinPool dollyForkJoinPool) {
        super(tpsfResponseHandler, tpsfService, tpsfPersonCache, identService, bestillingProgressRepository,
                bestillingService, mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry);

        this.bestillingService = bestillingService;
        this.errorStatusDecoder = errorStatusDecoder;
        this.mapperFacade = mapperFacade;
        this.tpsfService = tpsfService;
        this.dollyForkJoinPool = dollyForkJoinPool;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {

            TpsfBestilling tpsfBestilling = nonNull(bestKriterier.getTpsf()) ?
                    mapperFacade.map(bestKriterier.getTpsf(), TpsfBestilling.class) : new TpsfBestilling();
            tpsfBestilling.setAntall(1);

            dollyForkJoinPool.submit(() ->
                    Collections.nCopies(bestilling.getAntallIdenter(), true).parallelStream()
                            .filter(ident -> !bestillingService.isStoppet(bestilling.getId()))
                            .map(ident -> {

                                BestillingProgress progress = null;
                                try {
                                    List<String> leverteIdenter = tpsfService.opprettIdenterTpsf(tpsfBestilling);

                                    TpsPerson tpsPerson = buildTpsPerson(bestilling, leverteIdenter, null);
                                    progress = new BestillingProgress(bestilling.getId(), tpsPerson.getHovedperson());

                                    sendIdenterTilTPS(Lists.newArrayList(bestilling.getMiljoer().split(",")),
                                            leverteIdenter, bestilling.getGruppe(), progress);

                                    gjenopprettNonTpsf(tpsPerson, bestKriterier, progress, false);

                                } catch (RuntimeException e) {
                                    progress = BestillingProgress.builder()
                                            .bestillingId(bestilling.getId())
                                            .ident("?")
                                            .feil("NA:" + errorStatusDecoder.decodeRuntimeException(e))
                                            .build();
                                } finally {
                                    oppdaterProgress(bestilling, progress);
                                }

                                return null;
                            })
                            .collect(Collectors.toList())
            );

        } else {
            bestilling.setFeil("Feil: kunne ikke mappe JSON request, se logg!");
        }
        oppdaterBestillingFerdig(bestilling);
    }
}
