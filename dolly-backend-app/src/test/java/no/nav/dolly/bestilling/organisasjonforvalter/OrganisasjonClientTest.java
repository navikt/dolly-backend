package no.nav.dolly.bestilling.organisasjonforvalter;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonBestillingRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonBestillingResponse;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonDeployResponse;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.domain.resultset.organisasjon.RsSyntetiskeOrganisasjoner;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.OrganisasjonNummerService;
import no.nav.dolly.service.OrganisasjonProgressService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganisasjonClientTest {

    private static final String ORG_NUMMER = "123456789";
    private static final Long BESTILLING_ID = 123L;

    @Mock
    private static OrganisasjonConsumer organisasjonConsumer;

    @Mock
    private OrganisasjonNummerService organisasjonNummerService;

    @Mock
    private OrganisasjonProgressService organisasjonProgressService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @InjectMocks
    private OrganisasjonClient organisasjonClient;


    private OrganisasjonBestillingResponse response;
    private RsOrganisasjonBestilling bestilling;
    private OrganisasjonBestillingRequest request;

    @Before
    public void setUp() {

        response = OrganisasjonBestillingResponse.builder()
                .organisasjonsNummer(ORG_NUMMER)
                .build();

        OrganisasjonBestillingRequest.SyntetiskOrganisasjon requestOrganisasjon = OrganisasjonBestillingRequest.SyntetiskOrganisasjon.builder()
                .formaal("Testing")
                .build();

        OrganisasjonBestillingRequest.SyntetiskOrganisasjon requestOrganisasjon2 = OrganisasjonBestillingRequest.SyntetiskOrganisasjon.builder()
                .formaal("Testing")
                .build();

        OrganisasjonBestillingRequest.SyntetiskOrganisasjon underOrganisasjon = OrganisasjonBestillingRequest.SyntetiskOrganisasjon.builder()
                .formaal("underenhet")
                .build();

        requestOrganisasjon.setUnderenheter(Collections.singletonList(underOrganisasjon));

        request = OrganisasjonBestillingRequest.builder()
                .organisasjoner(List.of(requestOrganisasjon, requestOrganisasjon2))
                .build();


        bestilling = RsOrganisasjonBestilling.builder()
                .environments(List.of("q1"))
                .rsSyntetiskeOrganisasjoner(new RsSyntetiskeOrganisasjoner())
                .build();

        when(mapperFacade.map(any(RsSyntetiskeOrganisasjoner.class), eq(OrganisasjonBestillingRequest.class))).thenReturn(request);
        when(organisasjonConsumer.postOrganisasjon(any())).thenReturn(new ResponseEntity<>(response, HttpStatus.CREATED));
        when(organisasjonConsumer.deployOrganisasjon(any())).thenReturn(new ResponseEntity<>(OrganisasjonDeployResponse.builder()
                .orgStatus(Collections.singletonList(OrganisasjonDeployResponse.OrgStatus.builder().envStatus(Collections.singletonList(OrganisasjonDeployResponse.EnvStatus.builder().environment("q1").status("OK").build())).build()))
                .build(), HttpStatus.OK));
    }

    @Test
    public void should_return_created_and_correct_formaal_for_underorganisasjon() {

        organisasjonClient.opprett(bestilling, BESTILLING_ID);
        assertThat(request.getOrganisasjoner()).isNotEmpty();
        assertThat(request.getOrganisasjoner().get(0).getUnderenheter()).isNotEmpty();
        assertThat(request.getOrganisasjoner().get(0).getUnderenheter().get(0).getFormaal()).contains("underenhet");
        assertThat(request.getOrganisasjoner().get(1).getUnderenheter()).isEmpty();
    }

}
