package no.nav.dolly.api;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.service.DollyBestillingService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyBestillingFraIdenterRequest;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeMedBestillingId;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBasisBestilling;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;
import no.nav.dolly.provider.api.TestgruppeController;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.PersonService;
import no.nav.dolly.service.TestgruppeService;

@RunWith(MockitoJUnitRunner.class)
public class TestgruppeControllerTest {

    private static final String IDENT = "12345678901";
    private static final Long GRUPPE_ID = 1L;
    private static final Long BESTILLING_ID = 1L;

    @Mock
    private TestgruppeService testgruppeService;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private DollyBestillingService dollyBestillingService;

    @Mock
    private BestillingService bestillingService;

    @Mock
    private PersonService personService;

    @InjectMocks
    private TestgruppeController testgruppeController;

    @Test
    public void opprettTestgruppe() {
        RsOpprettEndreTestgruppe gruppe = new RsOpprettEndreTestgruppe();
        Testgruppe testgruppe = Testgruppe.builder().id(1L).build();
        when(testgruppeService.opprettTestgruppe(gruppe)).thenReturn(testgruppe);

        testgruppeController.opprettTestgruppe(gruppe);
        verify(testgruppeService).fetchTestgruppeById(1L);
    }

    @Test
    public void oppdaterTestgruppe() {

        RsOpprettEndreTestgruppe gruppe = new RsOpprettEndreTestgruppe();
        Testgruppe testgruppe = new Testgruppe();
        when(testgruppeService.oppdaterTestgruppe(GRUPPE_ID, gruppe)).thenReturn(testgruppe);

        testgruppeController.oppdaterTestgruppe(GRUPPE_ID, gruppe);

        verify(testgruppeService).oppdaterTestgruppe(GRUPPE_ID, gruppe);
    }

    @Test
    public void getTestgruppe() {
        RsTestgruppeMedBestillingId testgruppeMedBestillingId = new RsTestgruppeMedBestillingId();
        testgruppeMedBestillingId.setId(GRUPPE_ID);
        when(testgruppeService.fetchTestgruppeById(GRUPPE_ID)).thenReturn(new Testgruppe());
        when(mapperFacade.map(any(Testgruppe.class), eq(RsTestgruppeMedBestillingId.class))).thenReturn(testgruppeMedBestillingId);

        RsTestgruppeMedBestillingId result = testgruppeController.getTestgruppe(GRUPPE_ID);

        assertThat(result.getId(), is(equalTo(GRUPPE_ID)));

        verify(testgruppeService).fetchTestgruppeById(GRUPPE_ID);
        verify(mapperFacade).map(any(Testgruppe.class), eq(RsTestgruppeMedBestillingId.class));
    }

    @Test
    public void getTestgrupper() {
        testgruppeController.getTestgrupper("nav");
        verify(testgruppeService).getTestgruppeByBrukerId("nav");
    }

    @Test
    public void oppretteIdentBestilling() {
        int ant = 1;
        List<String> envir = singletonList("u");

        RsDollyBestillingRequest dollyBestillingRequest = new RsDollyBestillingRequest();
        dollyBestillingRequest.setTpsf(RsTpsfUtvidetBestilling.builder().build());
        dollyBestillingRequest.setAntall(ant);
        dollyBestillingRequest.setEnvironments(envir);

        Bestilling bestilling = Bestilling.builder().id(BESTILLING_ID).build();

        when(bestillingService.saveBestilling(eq(GRUPPE_ID), any(RsDollyBestilling.class), any(RsTpsfUtvidetBestilling.class), eq(ant), eq(null))).thenReturn(bestilling);

        testgruppeController.opprettIdentBestilling(GRUPPE_ID, dollyBestillingRequest);
        verify(dollyBestillingService).opprettPersonerByKriterierAsync(GRUPPE_ID, dollyBestillingRequest, bestilling);
    }

    @Test
    public void oppretteIdentBestillingFraEksisterende() {
        List<String> envir = singletonList("u");

        RsDollyBestillingFraIdenterRequest dollyBestillingsRequest = new RsDollyBestillingFraIdenterRequest();
        dollyBestillingsRequest.getOpprettFraIdenter().add(IDENT);
        dollyBestillingsRequest.setTpsf(RsTpsfUtvidetBestilling.builder().build());
        dollyBestillingsRequest.setEnvironments(envir);

        Bestilling bestilling = Bestilling.builder().id(BESTILLING_ID).build();

        when(bestillingService.saveBestilling(eq(GRUPPE_ID), any(RsDollyBestilling.class), any(RsTpsfBasisBestilling.class), eq(1), anyList())).thenReturn(bestilling);

        testgruppeController.opprettIdentBestillingFraIdenter(GRUPPE_ID, dollyBestillingsRequest);
        verify(dollyBestillingService).opprettPersonerFraIdenterMedKriterierAsync(GRUPPE_ID, dollyBestillingsRequest, bestilling);
    }
}