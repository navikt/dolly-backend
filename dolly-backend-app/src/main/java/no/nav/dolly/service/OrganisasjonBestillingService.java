package no.nav.dolly.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.BestilteKriterier;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.OrganisasjonBestillingProgressRepository;
import no.nav.dolly.repository.OrganisasjonBestillingRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.util.CurrentAuthentication.getUserId;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@RequiredArgsConstructor
public class OrganisasjonBestillingService {

    private final OrganisasjonBestillingRepository bestillingRepository;
    private final OrganisasjonBestillingProgressRepository bestillingProgressRepository;
    private final ObjectMapper objectMapper;

    public OrganisasjonBestilling fetchBestillingById(Long bestillingId) {
        return bestillingRepository.findById(bestillingId)
                .orElseThrow(() -> new NotFoundException(format("Fant ikke bestillingId %d", bestillingId)));
    }

    @Transactional
    public OrganisasjonBestilling saveBestillingToDB(OrganisasjonBestilling bestilling) {
        try {
            return bestillingRepository.save(bestilling);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("Kunne ikke lagre bestilling: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Bestilling saveBestilling(RsDollyUpdateRequest request, String ident) {

        Testident testident = identRepository.findByIdent(ident);
        if (isNull(testident) || isBlank(testident.getIdent())) {
            throw new NotFoundException(format("Testindent %s ble ikke funnet", ident));
        }
        fixAaregAbstractClassProblem(request.getAareg());
        fixPdlAbstractClassProblem(request.getPdlforvalter());
        return saveBestillingToDB(
                Bestilling.builder()
                        .gruppe(testident.getTestgruppe())
                        .ident(ident)
                        .antallIdenter(1)
                        .sistOppdatert(now())
                        .miljoer(join(",", request.getEnvironments()))
                        .tpsfKriterier(toJson(request.getTpsf()))
                        .bestKriterier(getBestKriterier(request))
                        .malBestillingNavn(request.getMalBestillingNavn())
                        .bruker(brukerService.fetchOrCreateBruker(getUserId()))
                        .build());
    }

    @Transactional
    public Bestilling saveBestilling(RsOrganisasjonBestilling request, Integer antall) {
        return saveBestillingToDB(
                OrganisasjonBestilling.builder()
                        .antall(antall)
                        .sistOppdatert(now())
                        .miljoer(join(",", request.getEnvironments()))
                        .bestKriterier(getBestKriterier(request))
                        .opprettFraIdenter(nonNull(opprettFraIdenter) ? join(",", opprettFraIdenter) : null)
                        .malBestillingNavn(request.getMalBestillingNavn())
                        .bruker(brukerService.fetchOrCreateBruker(getUserId()))
                        .build());
    }

    private String toJson(Object object) {
        try {
            if (nonNull(object)) {
                return objectMapper.writer().writeValueAsString(object);
            }
        } catch (JsonProcessingException | RuntimeException e) {
            log.info("Konvertering til Json feilet", e);
        }
        return null;
    }

    private String getBestKriterier(RsOrganisasjonBestilling request) {
        return toJson(BestilteKriterier.builder()
                .aareg(request.getAareg())
                .krrstub(request.getKrrstub())
                .udistub(request.getUdistub())
                .sigrunstub(request.getSigrunstub())
                .arenaforvalter(request.getArenaforvalter())
                .pdlforvalter(request.getPdlforvalter())
                .instdata(request.getInstdata())
                .inntektstub(request.getInntektstub())
                .pensjonforvalter(request.getPensjonforvalter())
                .inntektsmelding(request.getInntektsmelding())
                .brregstub(request.getBrregstub())
                .dokarkiv(request.getDokarkiv())
                .sykemelding(request.getSykemelding())
                .build());
    }
}