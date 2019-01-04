package no.nav.dolly.service;

import static java.util.Objects.isNull;
import static no.nav.dolly.util.CurrentNavIdentFetcher.getLoggedInNavIdent;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.stereotype.Service;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsOpprettEndreTestgruppe;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.GruppeRepository;

@Service
public class TestgruppeService {

    @Autowired
    private GruppeRepository gruppeRepository;

    @Autowired
    private BrukerService brukerService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private IdentService identService;

    public Testgruppe opprettTestgruppe(RsOpprettEndreTestgruppe rsTestgruppe) {
        Bruker bruker = brukerService.fetchBruker(getLoggedInNavIdent());

        return saveGruppeTilDB(Testgruppe.builder()
                .navn(rsTestgruppe.getNavn())
                .hensikt(rsTestgruppe.getHensikt())
                .teamtilhoerighet(teamService.fetchTeamOrOpprettBrukerteam(rsTestgruppe.getTeamId()))
                .datoEndret(LocalDate.now())
                .opprettetAv(bruker)
                .sistEndretAv(bruker)
                .build()
        );
    }

    public Testgruppe fetchTestgruppeById(Long gruppeId) {
        return gruppeRepository.findById(gruppeId).orElseThrow(() -> new NotFoundException("Finner ikke gruppe basert på gruppeID: " + gruppeId));
    }

    public List<Testgruppe> fetchGrupperByIdsIn(Collection<Long> grupperIDer) {
        List<Testgruppe> grupper = gruppeRepository.findAllById(grupperIDer);
        if (!grupper.isEmpty()) {
            return grupper;
        }
        throw new NotFoundException("Finner ikke grupper basert på IDer : " + grupperIDer);
    }

    public List<Testgruppe> fetchTestgrupperByNavIdent(String navIdent) {
        Bruker bruker = brukerService.fetchBruker(navIdent);
        Set<Testgruppe> testgrupper = bruker.getFavoritter();
        bruker.getTeams().forEach(team -> testgrupper.addAll(team.getGrupper()));

        List<Testgruppe> unikeTestgrupper = new ArrayList(testgrupper);
        Collections.sort(unikeTestgrupper,(Testgruppe tg1, Testgruppe tg2) -> tg1.getNavn().compareToIgnoreCase(tg2.getNavn()));

        return unikeTestgrupper;
    }

    public Testgruppe saveGruppeTilDB(Testgruppe testgruppe) {
        try {
            return gruppeRepository.save(testgruppe);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("En Testgruppe DB constraint er brutt! Kan ikke lagre testgruppe. Error: " + e.getMessage(), e);
        } catch (NonTransientDataAccessException e) {
            throw new DollyFunctionalException(e.getRootCause().getMessage(), e);
        }
    }

    public List<Testgruppe> saveGrupper(Collection<Testgruppe> testgrupper) {
        try {
            return gruppeRepository.saveAll(testgrupper);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("En Testgruppe DB constraint er brutt! Kan ikke lagre testgruppe. Error: " + e.getMessage(), e);
        } catch (NonTransientDataAccessException e) {
            throw new DollyFunctionalException(e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage(), e);
        }
    }

    public int slettGruppeById(Long gruppeId) {
        identService.slettTestidenterByGruppeId(gruppeId);
        brukerService.sletteBrukerFavoritterByGroupId(gruppeId);
        return gruppeRepository.deleteTestgruppeById(gruppeId);
    }

    public int slettGruppeByTeamId(Long teamId) {
        identService.slettTestidenterByTeamId(teamId);
        brukerService.sletteBrukerFavoritterByTeamId(teamId);
        return gruppeRepository.deleteTestgruppeByTeamtilhoerighetId(teamId);
    }

    public Testgruppe oppdaterTestgruppe(Long gruppeId, RsOpprettEndreTestgruppe endreGruppe) {
        Testgruppe testgruppe = fetchTestgruppeById(gruppeId);

        testgruppe.setHensikt(endreGruppe.getHensikt());
        testgruppe.setNavn(endreGruppe.getNavn());
        testgruppe.setSistEndretAv(brukerService.fetchBruker(getLoggedInNavIdent()));
        testgruppe.setTeamtilhoerighet(teamService.fetchTeamById(endreGruppe.getTeamId()));
        testgruppe.setDatoEndret(LocalDate.now());

        return saveGruppeTilDB(testgruppe);
    }

    public List<Testgruppe> getTestgruppeByNavidentOgTeamId(String navIdent, Long teamId) {
        List<Testgruppe> grupper;
        if (isNull(teamId)) {
            grupper = isBlank(navIdent) ? gruppeRepository.findAllByOrderByNavn() : fetchTestgrupperByNavIdent(navIdent);
        } else {
            grupper = gruppeRepository.findAllByTeamtilhoerighetOrderByNavn(Team.builder().id(teamId).build());
        }

        return grupper;
    }

    public List<String> fetchIdenterByGruppeId(Long gruppeId) {
        return fetchTestgruppeById(gruppeId)
                .getTestidenter()
                .stream()
                .map(ident -> ident.getIdent())
                .collect(Collectors.toList());
    }
}
