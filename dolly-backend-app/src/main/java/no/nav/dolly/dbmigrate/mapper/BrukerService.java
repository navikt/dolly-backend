package no.nav.dolly.dbmigrate.mapper;

import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.oracle.OraBruker;
import no.nav.dolly.domain.jpa.postgres.Bruker;
import no.nav.dolly.repository.oracle.OraBrukerRepository;
import no.nav.dolly.repository.postgres.BrukerRepository;

@Service
@RequiredArgsConstructor
public class BrukerService {

    private final BrukerRepository brukerRepository;
    private final OraBrukerRepository oraBrukerRepository;

    public void migrate() {
        Iterable<OraBruker> oraBrukereInput = oraBrukerRepository.findAll(Sort.by("id"));
        List<OraBruker> oraBrukere = new ArrayList<>();
        Map<String, Bruker> brukere = new HashMap<>();
        oraBrukereInput.forEach(bruker -> {
            oraBrukere.add(bruker);
            brukere.put(nonNull(bruker.getBrukerId()) ? bruker.getBrukerId() : bruker.getNavIdent(),
                    brukerRepository.save(mapBruker(bruker)));
        });

        // Legg til relasjoner
        oraBrukere.forEach(bruker -> {
            if (nonNull(bruker.getEidAv())) {
                Bruker brukerMedRelasjon = brukere.get(bruker.getNavIdent());
                brukerMedRelasjon.setEidAv(brukere.get(bruker.getEidAv().getBrukerId()));
                brukerRepository.save(brukerMedRelasjon);
            }
        });

    }

    private static Bruker mapBruker(OraBruker bruker) {

        return Bruker.builder()
                .id(bruker.getId())
                .brukerId(bruker.getBrukerId())
                .brukernavn(bruker.getBrukernavn())
                .epost(bruker.getEpost())
                .navIdent(bruker.getNavIdent())
                .migrert(bruker.getMigrert())
                .build();
    }
}
