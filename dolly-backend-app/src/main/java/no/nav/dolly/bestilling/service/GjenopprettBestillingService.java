package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.tpsf.TpsfResponseHandler;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.DollyPersonCache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

@Service
public class GjenopprettBestillingService extends DollyBestillingService {

    private BestillingService bestillingService;
    private ErrorStatusDecoder errorStatusDecoder;
    private TpsfService tpsfService;
    private BestillingProgressService bestillingProgressService;
    private ExecutorService dollyForkJoinPool;
    private DollyPersonCache dollyPersonCache;

    public GjenopprettBestillingService(TpsfResponseHandler tpsfResponseHandler, TpsfService tpsfService, DollyPersonCache dollyPersonCache,
                                        IdentService identService, BestillingProgressService bestillingProgressService,
                                        BestillingService bestillingService, MapperFacade mapperFacade, CacheManager cacheManager,
                                        ObjectMapper objectMapper, List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                        ErrorStatusDecoder errorStatusDecoder, ExecutorService dollyForkJoinPool) {
        super(tpsfResponseHandler, tpsfService, dollyPersonCache, identService, bestillingProgressService, bestillingService,
                mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry);

        this.bestillingService = bestillingService;
        this.errorStatusDecoder = errorStatusDecoder;
        this.tpsfService = tpsfService;
        this.bestillingProgressService = bestillingProgressService;
        this.dollyForkJoinPool = dollyForkJoinPool;
        this.dollyPersonCache = dollyPersonCache;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {
            dollyForkJoinPool.submit(() -> {
                bestillingProgressService.fetchBestillingProgressByBestillingId(bestilling.getOpprettetFraId()).parallelStream()
                        .filter(ident -> !bestillingService.isStoppet(bestilling.getId()))
                        .map(gjenopprettFraProgress -> {

                            BestillingProgress progress = new BestillingProgress(bestilling.getId(), gjenopprettFraProgress.getIdent());
                            try {
                                List<Person> personer = tpsfService.hentTestpersoner(singletonList(gjenopprettFraProgress.getIdent()));

                                if (!personer.isEmpty()) {
                                    DollyPerson dollyPerson = dollyPersonCache.prepareTpsPersoner(personer.get(0));
                                    sendIdenterTilTPS(new ArrayList<>(List.of(bestilling.getMiljoer().split(","))),
                                            Stream.of(List.of(dollyPerson.getHovedperson()), dollyPerson.getPartnere(), dollyPerson.getBarn())
                                                    .flatMap(Collection::stream)
                                                    .collect(Collectors.toList()),
                                            bestilling.getGruppe(), progress);

                                    gjenopprettNonTpsf(dollyPerson, bestKriterier, progress, false);
                                } else {
                                    progress.setFeil("NA:Feil= Finner ikke personen i database");
                                }

                            } catch (RuntimeException e) {
                                progress.setFeil(errorStatusDecoder.decodeRuntimeException(e));

                            } finally {
                                oppdaterProgress(bestilling, progress);
                            }
                            return null;
                        })
                        .collect(Collectors.toList());
                oppdaterBestillingFerdig(bestilling);
            });
        } else {
            bestilling.setFeil("Feil: kunne ikke mappe JSON request, se logg!");
            oppdaterBestillingFerdig(bestilling);
        }
    }
}
