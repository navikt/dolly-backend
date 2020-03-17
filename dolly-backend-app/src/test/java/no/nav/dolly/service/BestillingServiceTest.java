package no.nav.dolly.service;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.util.Sets.newHashSet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingKontroll;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingKontrollRepository;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@RunWith(MockitoJUnitRunner.class)
public class BestillingServiceTest {

    private static final String CURRENT_BRUKER_IDENT = "NAV1";
    private static final long BEST_ID = 1L;

    @Mock
    private BestillingRepository bestillingRepository;

    @Mock
    private BestillingKontrollRepository bestillingKontrollRepository;

    @Mock
    private IdentRepository identRepository;

    @Mock
    private BestillingProgressRepository bestillingProgressRepository;

    @Mock
    private TestgruppeRepository testgruppeRepository;

    @InjectMocks
    private BestillingService bestillingService;

    @BeforeClass
    public static void beforeClass() {
        SecurityContextHolder.getContext().setAuthentication(
                new OidcTokenAuthentication(CURRENT_BRUKER_IDENT, null, null, null, null));
    }

    @Test(expected = NotFoundException.class)
    public void fetchBestillingByIdKasterExceptionHvisBestillingIkkeFunnet() {
        Optional<Bestilling> bes = Optional.empty();

        when(bestillingRepository.findById(any())).thenReturn(bes);

        bestillingService.fetchBestillingById(1l);
    }

    @Test
    public void fetchBestillingByIdKasterReturnererBestillingHvisBestillingErFunnet() {
        Bestilling mock = mock(Bestilling.class);
        Optional<Bestilling> bes = Optional.of(mock);

        when(bestillingRepository.findById(any())).thenReturn(bes);

        Bestilling bestilling = bestillingService.fetchBestillingById(1l);

        assertThat(bestilling, is(mock));
    }

    @Test(expected = ConstraintViolationException.class)
    public void saveBestillingToDBKasterExceptionHvisDBConstraintBlirBrutt() {
        when(bestillingRepository.save(any())).thenThrow(DataIntegrityViolationException.class);
        bestillingService.saveBestillingToDB(new Bestilling());
    }

    @Test
    public void fetchBestillingerByGruppeIdBlirKaltMedGittFunnetTestgruppeOgReturnererBestillinger() {
        Testgruppe gruppe = mock(Testgruppe.class);
        when(testgruppeRepository.findById(any())).thenReturn(Optional.of(gruppe));

        bestillingService.fetchBestillingerByGruppeId(1l);
        verify(testgruppeRepository).findById(1l);
    }

    @Test
    public void saveBestillingByGruppeIdAndAntallIdenterInkludererAlleMiljoerOgIdenterIBestilling() {
        long gruppeId = 1l;
        Testgruppe gruppe = mock(Testgruppe.class);
        List<String> miljoer = asList("a1", "b2", "c3", "d4");
        int antallIdenter = 4;

        when(testgruppeRepository.findById(gruppeId)).thenReturn(Optional.of(gruppe));

        bestillingService.saveBestilling(gruppeId, RsDollyBestilling.builder().environments(miljoer).build(),
                RsTpsfUtvidetBestilling.builder().build(), antallIdenter, null, "Trudelu");

        ArgumentCaptor<Bestilling> argCap = ArgumentCaptor.forClass(Bestilling.class);
        verify(bestillingRepository).save(argCap.capture());

        Bestilling bes = argCap.getValue();

        assertThat(bes.getGruppe(), is(gruppe));
        assertThat(bes.getAntallIdenter(), is(antallIdenter));
        assertThat(bes.getMiljoer(), is("a1,b2,c3,d4"));
    }

    @Test
    public void cancelBestilling_OK() {

        when(bestillingRepository.findById(BEST_ID)).thenReturn(Optional.of(Bestilling.builder().build()));
        bestillingService.cancelBestilling(1L);

        verify(bestillingKontrollRepository).findByBestillingIdOrderByBestillingId(BEST_ID);
        verify(bestillingKontrollRepository).save(any(BestillingKontroll.class));
        verify(identRepository).deleteTestidentsByBestillingId(BEST_ID);
        verify(bestillingProgressRepository).deleteByBestillingId(BEST_ID);
    }

    @Test(expected = NotFoundException.class)
    public void cancelBestilling_NotFound() {

        when(bestillingRepository.findById(BEST_ID)).thenThrow(NotFoundException.class);
        bestillingService.cancelBestilling(1L);
    }

    @Test
    public void createBestillingForGjenopprett_Ok() {

        when(bestillingRepository.findById(BEST_ID)).thenReturn(Optional.of(Bestilling.builder()
                .gruppe(Testgruppe.builder()
                        .testidenter(newHashSet(asList(Testident.builder().build()))).build())
                .ferdig(true).build()));

        bestillingService.createBestillingForGjenopprett(BEST_ID, singletonList("u1"));

        verify(bestillingRepository).save(any(Bestilling.class));
    }

    @Test(expected = DollyFunctionalException.class)
    public void createBestillingForGjenopprett_notFerdig() {

        when(bestillingRepository.findById(BEST_ID)).thenReturn(Optional.of(Bestilling.builder().build()));

        bestillingService.createBestillingForGjenopprett(BEST_ID, singletonList("u1"));
    }

    @Test(expected = NotFoundException.class)
    public void createBestillingForGjenopprett_noTestidenter() {

        when(bestillingRepository.findById(BEST_ID)).thenReturn(Optional.of(
                Bestilling.builder().ferdig(true)
                        .gruppe(Testgruppe.builder().build())
                        .build()));

        bestillingService.createBestillingForGjenopprett(BEST_ID, singletonList("u1"));
    }

    @Test
    public void isStoppet_OK() {

        bestillingService.isStoppet(BEST_ID);

        verify(bestillingKontrollRepository).findByBestillingIdOrderByBestillingId(BEST_ID);
    }
}