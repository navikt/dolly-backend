package no.nav.dolly.bestilling.organisasjonforvalter;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonResponse;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.domain.resultset.organisasjon.RsOrganisasjoner;
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


    private OrganisasjonResponse response;
    private RsOrganisasjonBestilling bestilling;
    private OrganisasjonRequest request;
    private OrganisasjonBestillingProgress progress;

    @Before
    public void setUp() {

        response = OrganisasjonResponse.builder()
                .organisasjonsNummer(ORG_NUMMER)
                .build();

        progress = new OrganisasjonBestillingProgress();

        OrganisasjonRequest.Organisasjon requestOrganisasjon = OrganisasjonRequest.Organisasjon.builder()
                .formaal("Testing")
                .build();

        OrganisasjonRequest.Organisasjon requestOrganisasjon2 = OrganisasjonRequest.Organisasjon.builder()
                .formaal("Testing")
                .build();

        OrganisasjonRequest.Organisasjon underOrganisasjon = OrganisasjonRequest.Organisasjon.builder()
                .formaal("underenhet")
                .build();

        requestOrganisasjon.setUnderenheter(Collections.singletonList(underOrganisasjon));

        request = OrganisasjonRequest.builder()
                .organisasjoner(List.of(requestOrganisasjon, requestOrganisasjon2))
                .build();


        bestilling = RsOrganisasjonBestilling.builder()
                .environments(List.of("q1"))
                .rsOrganisasjoner(new RsOrganisasjoner())
                .build();

        when(mapperFacade.map(any(RsOrganisasjoner.class), eq(OrganisasjonRequest.class))).thenReturn(request);
        when(organisasjonConsumer.postOrganisasjon(any())).thenReturn(new ResponseEntity<>(response, HttpStatus.CREATED));
    }

    @Test
    public void should_return_created_and_correct_id_for_underorganisasjon() {


        organisasjonClient.opprett(bestilling, progress);
        assertThat(progress.getOrganisasjonsforvalterStatus()).isNotBlank().contains("OK");
        assertThat(request.getOrganisasjoner().get(0).getUnderenheter().get(0).getId()).contains("1");
        assertThat(request.getOrganisasjoner().get(0).getUnderenheter().get(0).getParentId()).contains("0");
    }

}
