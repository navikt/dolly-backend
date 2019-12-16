package no.nav.dolly.service;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;
import static no.nav.dolly.security.sts.StsOidcService.getUserPrinciple;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingKontroll;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.BestilteKriterier;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.domain.resultset.pdlforvalter.RsPdldata;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBasisBestilling;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingKontrollRepository;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;

@Slf4j
@Service
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@RequiredArgsConstructor
public class BestillingService {

    private final BestillingRepository bestillingRepository;
    private final BestillingKontrollRepository bestillingKontrollRepository;
    private final IdentRepository identRepository;
    private final BestillingProgressRepository bestillingProgressRepository;
    private final ObjectMapper objectMapper;
    private final TestgruppeRepository testgruppeRepository;

    public Bestilling fetchBestillingById(Long bestillingId) {
        return bestillingRepository.findById(bestillingId).orElseThrow(() -> new NotFoundException(format("Fant ikke bestillingId %d", bestillingId)));
    }

    @Transactional
    public Bestilling saveBestillingToDB(Bestilling bestilling) {
        try {
            return bestillingRepository.save(bestilling);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("Kunne ikke lagre bestilling: " + e.getMessage(), e);
        }
    }

    public List<Bestilling> fetchBestillingerByGruppeId(Long gruppeId) {
        Optional<Testgruppe> testgruppe = testgruppeRepository.findById(gruppeId);
        return testgruppe.isPresent() ?
                bestillingRepository.findBestillingByGruppeOrderById(testgruppe.get()) : emptyList();
    }

    public List<Bestilling> fetchMalBestillinger() {
        String userId = getContext().getAuthentication().getPrincipal().toString();
        return bestillingRepository.findMalBestilling(userId).orElse(emptyList());
    }

    @Transactional
    public Bestilling cancelBestilling(Long bestillingId) {
        Optional<BestillingKontroll> bestillingKontroll = bestillingKontrollRepository.findByBestillingIdOrderByBestillingId(bestillingId);
        if (!bestillingKontroll.isPresent()) {
            bestillingKontrollRepository.save(BestillingKontroll.builder()
                    .bestillingId(bestillingId)
                    .stoppet(true)
                    .build());
        }

        Bestilling bestilling = fetchBestillingById(bestillingId);
        bestilling.setStoppet(true);
        bestilling.setFerdig(true);
        bestilling.setSistOppdatert(now());
        bestilling.setUserId(getUserPrinciple());
        saveBestillingToDB(bestilling);
        identRepository.deleteTestidentsByBestillingId(bestillingId);
        bestillingProgressRepository.deleteByBestillingId(bestillingId);
        return bestilling;
    }

    public boolean isStoppet(Long bestillingId) {
        return bestillingKontrollRepository.findByBestillingIdOrderByBestillingId(bestillingId).orElse(BestillingKontroll.builder().stoppet(false).build()).isStoppet();
    }

    @Transactional
    public Bestilling saveBestilling(RsDollyUpdateRequest request) {

        Testident testident = identRepository.findByIdent(request.getIdent());
        if (isNull(testident) || isBlank(testident.getIdent())) {
            throw new NotFoundException(format("Testindent %s ble ikke funnet", request.getIdent()));
        }
        fixPdlAbstractClassProblem(request.getPdlforvalter());
        return saveBestillingToDB(
                Bestilling.builder()
                        .gruppe(testident.getTestgruppe())
                        .ident(request.getIdent())
                        .antallIdenter(1)
                        .sistOppdatert(now())
                        .miljoer(join(",", request.getEnvironments()))
                        .tpsfKriterier(toJson(request.getTpsf()))
                        .bestKriterier(toJson(BestilteKriterier.builder()
                                .aareg(request.getAareg())
                                .krrstub(request.getKrrstub())
                                .udistub(request.getUdistub())
                                .sigrunstub(request.getSigrunstub())
                                .arenaforvalter(request.getArenaforvalter())
                                .pdlforvalter(request.getPdlforvalter())
                                .instdata(request.getInstdata())
                                .inntektsstub(request.getInntektsstub())
                                .build()))
                        .userId(getUserPrinciple())
                        .build());
    }

    @Transactional
    public Bestilling saveBestilling(Long gruppeId, RsDollyBestilling request, RsTpsfBasisBestilling tpsf, Integer antall, List<String> opprettFraIdenter) {
        Testgruppe gruppe = testgruppeRepository.findById(gruppeId).orElseThrow(() -> new NotFoundException("Finner ikke gruppe basert på gruppeID: " + gruppeId));
        fixPdlAbstractClassProblem(request.getPdlforvalter());
        return saveBestillingToDB(
                Bestilling.builder()
                        .gruppe(gruppe)
                        .antallIdenter(antall)
                        .sistOppdatert(now())
                        .miljoer(join(",", request.getEnvironments()))
                        .tpsfKriterier(toJson(tpsf))
                        .bestKriterier(toJson(BestilteKriterier.builder()
                                .aareg(request.getAareg())
                                .krrstub(request.getKrrstub())
                                .udistub(request.getUdistub())
                                .sigrunstub(request.getSigrunstub())
                                .arenaforvalter(request.getArenaforvalter())
                                .pdlforvalter(request.getPdlforvalter())
                                .instdata(request.getInstdata())
                                .inntektsstub(request.getInntektstub())
                                .build()))
                        .opprettFraIdenter(nonNull(opprettFraIdenter) ? join(",", opprettFraIdenter) : null)
                        .malBestillingNavn(request.getMalBestillingNavn())
                        .userId(getUserPrinciple())
                        .build());
    }

    @Transactional
    // Egen transaksjon på denne da bestillingId hentes opp igjen fra database i samme kallet
    public Bestilling createBestillingForGjenopprett(Long bestillingId, List<String> miljoer) {
        Bestilling bestilling = fetchBestillingById(bestillingId);
        if (!bestilling.isFerdig()) {
            throw new DollyFunctionalException(format("Du kan ikke starte gjenopprett før bestilling %d er ferdigstilt.", bestillingId));
        }
        if (bestilling.getGruppe().getTestidenter().isEmpty()) {
            throw new NotFoundException(format("Ingen testidenter funnet på bestilling: %d", bestillingId));
        }
        return saveBestillingToDB(
                Bestilling.builder()
                        .gruppe(bestilling.getGruppe())
                        .antallIdenter(bestilling.getAntallIdenter())
                        .opprettFraIdenter(bestilling.getOpprettFraIdenter())
                        .sistOppdatert(now())
                        .miljoer(miljoer.isEmpty() ? bestilling.getMiljoer() : join(",", miljoer))
                        .opprettetFraId(bestillingId)
                        .tpsfKriterier(bestilling.getTpsfKriterier())
                        .bestKriterier(bestilling.getBestKriterier())
                        .userId(getUserPrinciple())
                        .build()
        );
    }

    public void slettBestillingerByGruppeId(Long gruppeId) {

        bestillingKontrollRepository.deleteByGruppeId(gruppeId);
        bestillingProgressRepository.deleteByGruppeId(gruppeId);
        bestillingRepository.deleteByGruppeId(gruppeId);
    }

    public void slettBestillingByTestIdent(String ident) {

        List<BestillingProgress> bestillingProgresses = bestillingProgressRepository.findByIdent(ident);
        bestillingProgressRepository.deleteByIdent(ident);

        Set<Long> bestillingIds = bestillingProgresses.stream().map(BestillingProgress::getBestillingId).collect(toSet());

        bestillingIds.forEach(id -> {

            bestillingKontrollRepository.deleteByBestillingWithNoChildren(id);
            bestillingRepository.deleteBestillingWithNoChildren(id);
        });
    }

    private static void fixPdlAbstractClassProblem(RsPdldata pdldata) {

        if (nonNull(pdldata)) {
            if (nonNull(pdldata.getKontaktinformasjonForDoedsbo())) {
                pdldata.getKontaktinformasjonForDoedsbo().setAdressat(pdldata.getKontaktinformasjonForDoedsbo().getAdressat());
            }
            if (nonNull(pdldata.getFalskIdentitet())) {
                pdldata.getFalskIdentitet().setRettIdentitet(pdldata.getFalskIdentitet().getRettIdentitet());
            }
        }
    }

    private String toJson(Object object) {
        try {
            if (nonNull(object)) {
                return objectMapper.writer().writeValueAsString(object);
            }
        } catch (JsonProcessingException | RuntimeException e) {
            log.debug("Konvertering til Json feilet", e);
        }
        return null;
    }
}