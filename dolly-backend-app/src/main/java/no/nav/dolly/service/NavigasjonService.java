package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.domain.resultset.entity.testident.RsWhereAmI;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.Relasjon;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.IdentRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NavigasjonService {

    private final IdentRepository identRepository;
    private final IdentService identService;
    private final TpsfService tpsfService;
    private final MapperFacade mapperFacade;

    public RsWhereAmI navigerTilIdent(String ident) {

        List<Person> familie = tpsfService.hentTestpersoner(List.of(ident));
        Set<String> identer = new HashSet<>(Set.of(ident));
        identer.addAll(familie.stream().map(Person::getRelasjoner)
                .flatMap(relasjoner -> relasjoner.stream().map(Relasjon::getPersonRelasjonMed).map(Person::getIdent))
                .collect(Collectors.toSet()));

        List<Testident> identsFound = identRepository.findByIdentIn(identer);
        if (!identsFound.isEmpty()) {
            Page<Testident> bestillingerFromGruppePaginert = identService.getBestillingerFromGruppePaginert(identsFound.get(0).getTestgruppe().getId(), 0, 5000);

            Optional<Testident> first = bestillingerFromGruppePaginert.stream().filter(testident -> testident.getIdent().equals(identsFound.get(0).getIdent())).findFirst();
            int i = bestillingerFromGruppePaginert.toList().indexOf(first.orElse(null));

            return RsWhereAmI.builder()
                    .gruppe(mapperFacade.map(identsFound.get(0).getTestgruppe(), RsTestgruppe.class))
                    .identHovedperson(identsFound.get(0).getIdent())
                    .identNavigerTil(ident)
                    .sidetall(Math.floorDiv(i, 10))
                    .build();
        } else {

            throw new NotFoundException(ident + " ble ikke funnet i database");
        }
    }
}
