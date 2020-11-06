package no.nav.dolly.service;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.postgres.Testgruppe;
import no.nav.dolly.domain.jpa.postgres.Testident;
import no.nav.dolly.domain.resultset.entity.testident.RsTestident;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.postgres.IdentRepository;

@Service
@RequiredArgsConstructor
public class IdentService {

    private final IdentRepository identRepository;
    private final MapperFacade mapperFacade;

    @Transactional
    public void persisterTestidenter(List<RsTestident> personIdentListe) {

        List<Testident> testidentList = mapperFacade.mapAsList(personIdentListe, Testident.class);
        try {
            identRepository.saveAll(testidentList);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("En Ident DB constraint er brutt! Kan ikke lagre Identer. Error: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Testident saveIdentTilGruppe(String ident, Testgruppe testgruppe) {

        Testident testident = identRepository.findByIdent(ident);
        if (isNull(testident)) {
            testident = new Testident();
        }
        testident.setIdent(ident);
        testident.setTestgruppe(testgruppe);
        return identRepository.save(testident);
    }

    public int slettTestident(String ident) {

        return identRepository.deleteTestidentByIdent(ident);
    }

    public int slettTestidenterByGruppeId(Long gruppeId) {

        return identRepository.deleteTestidentByTestgruppeId(gruppeId);
    }

    @Transactional
    public Testident save(String ident, boolean iBruk) {

        Testident testident = identRepository.findByIdent(ident);
        if (nonNull(testident)) {
            testident.setIBruk(iBruk);
            return identRepository.save(testident);
        } else {
            throw new NotFoundException(format("Testperson med ident %s ble ikke funnet.", ident));
        }
    }

    @Transactional
    public Testident save(String ident, String beskrivelse) {

        Testident testident = identRepository.findByIdent(ident);
        if (nonNull(testident)) {
            testident.setBeskrivelse(beskrivelse);
            return identRepository.save(testident);
        } else {
            throw new NotFoundException(format("Testperson med ident %s ble ikke funnet.", ident));
        }
    }
}