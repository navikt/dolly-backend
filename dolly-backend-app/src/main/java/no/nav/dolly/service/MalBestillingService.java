package no.nav.dolly.service;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Slf4j
@RequiredArgsConstructor
public class MalBestillingService {

    private static final String COMMON = "FELLES";
    private static final String ALLE = "ALLE";

    private final BestillingService bestillingService;
    private final MapperFacade mapperFacade;

    public RsMalBestillingWrapper getMalBestillinger() {

        RsMalBestillingWrapper malBestillingWrapper = new RsMalBestillingWrapper();

        List<Bestilling> bestillinger = bestillingService.fetchMalBestillinger();
        malBestillingWrapper.getMalbestillinger().putIfAbsent(ALLE,
                new TreeSet<>(Comparator.comparing(RsMalBestillingWrapper.RsMalBestilling::getMalNavn)
                        .thenComparing(RsMalBestillingWrapper.RsMalBestilling::getId)));
        bestillinger.forEach(bestilling -> {

            RsMalBestillingWrapper.RsBestilling rsBestilling = mapperFacade.map(bestilling, RsMalBestillingWrapper.RsBestilling.class);

            setArenaforvalterEmptyListsToNull(rsBestilling);

            RsMalBestillingWrapper.RsMalBestilling malBestilling = RsMalBestillingWrapper.RsMalBestilling.builder()
                    .malNavn(bestilling.getMalBestillingNavn())
                    .bruker(mapperFacade.map(nonNull(bestilling.getBruker()) ? bestilling.getBruker() :
                            Bruker.builder().brukerId(COMMON).brukernavn(COMMON).build(), RsBrukerUtenFavoritter.class))
                    .id(bestilling.getId())
                    .bestilling(rsBestilling)
                    .build();

            malBestillingWrapper.getMalbestillinger().putIfAbsent(getUserId(bestilling.getBruker()),
                    new TreeSet<>(Comparator.comparing(RsMalBestillingWrapper.RsMalBestilling::getMalNavn)
                            .thenComparing(RsMalBestillingWrapper.RsMalBestilling::getId)));
            malBestillingWrapper.getMalbestillinger().get(getUserId(bestilling.getBruker())).add(malBestilling);
            malBestillingWrapper.getMalbestillinger().get(ALLE).add(malBestilling);
        });

        return malBestillingWrapper;
    }

    private void setArenaforvalterEmptyListsToNull(RsMalBestillingWrapper.RsBestilling rsBestilling) {
        if (isNull(rsBestilling.getArenaforvalter())) {
            return;
        }
        log.info("rsbestilling arena før filtrering: {}", Json.pretty(rsBestilling.getArenaforvalter()));
        if (rsBestilling.getArenaforvalter().getAap().isEmpty()) {
            rsBestilling.getArenaforvalter().setAap(null);
        }
        if (rsBestilling.getArenaforvalter().getAap115().isEmpty()) {
            rsBestilling.getArenaforvalter().setAap115(null);
        }
        if (rsBestilling.getArenaforvalter().getDagpenger().isEmpty()) {
            rsBestilling.getArenaforvalter().setDagpenger(null);
        }
        log.info("Etter filtrering: {}", Json.pretty(rsBestilling.getArenaforvalter()));
    }

    private static String getUserId(Bruker bruker) {

        return nonNull(bruker) ? resolveId(bruker) : COMMON;
    }

    private static String resolveId(Bruker bruker) {

        if (nonNull(bruker.getEidAv())) {
            return bruker.getEidAv().getBrukernavn();

        } else if (isNotBlank(bruker.getBrukernavn())) {
            return bruker.getBrukernavn();

        } else {
            return bruker.getNavIdent();
        }
    }
}
