package no.nav.dolly.bestilling.organisasjonforvalter;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingResponse;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployResponse;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.service.OrganisasjonNummerService;
import no.nav.dolly.service.OrganisasjonProgressService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganisasjonClientTest {

    private static final String ORG_NUMMER = "123456789";
    private static final String ORG_NUMMER_TO = "987654321";
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


    private DeployResponse deployResponse;
    private RsOrganisasjonBestilling bestilling;

    @Before
    public void setUp() {

        deployResponse = DeployResponse.builder()
                .orgStatus(Collections.singletonList(DeployResponse.OrgStatus.builder()
                        .orgnummer(ORG_NUMMER)
                        .envStatus(Collections.singletonList(DeployResponse.EnvStatus.builder()
                                .environment("q1")
                                .status("OK")
                                .build()))
                        .build()))
                .build();

        BestillingRequest.SyntetiskOrganisasjon requestOrganisasjon = BestillingRequest.SyntetiskOrganisasjon.builder()
                .formaal("Testing")
                .build();

        BestillingRequest.SyntetiskOrganisasjon underOrganisasjon = BestillingRequest.SyntetiskOrganisasjon.builder()
                .formaal("underenhet")
                .build();

        requestOrganisasjon.setUnderenheter(Collections.singletonList(underOrganisasjon));

        RsOrganisasjonBestilling.SyntetiskOrganisasjon.Adresse adresse = RsOrganisasjonBestilling.SyntetiskOrganisasjon.Adresse.builder()
                .postnr("1234")
                .landkode("NO")
                .kommunenr("123")
                .adresselinjer(Collections.singletonList("Gate 1"))
                .build();

        bestilling = RsOrganisasjonBestilling.builder()
                .environments(List.of("q1"))
                .organisasjoner(Collections.singletonList(
                        RsOrganisasjonBestilling.SyntetiskOrganisasjon.builder()
                                .forretningsadresse(adresse)
                                .postadresse(adresse)
                                .build()))
                .build();

        Stack<String> orgnummer = new Stack<>();
        orgnummer.push(ORG_NUMMER);
        orgnummer.push(ORG_NUMMER_TO);

        when(mapperFacade.mapAsList(anyList(), eq(BestillingRequest.SyntetiskOrganisasjon.class))).thenReturn(List.of(requestOrganisasjon, requestOrganisasjon));
        when(organisasjonConsumer.postOrganisasjon(any())).thenReturn(new ResponseEntity<>(new BestillingResponse(orgnummer.pop()), HttpStatus.CREATED));
        when(organisasjonConsumer.deployOrganisasjon(any())).thenReturn(new ResponseEntity<>(deployResponse, HttpStatus.OK));
    }

    @Test
    public void should_run_deploy_organisasjon_exactly_one_time_for_two_hovedorganisasjoner() {

        organisasjonClient.opprett(bestilling, BESTILLING_ID);

        verify(organisasjonConsumer, times(1).description("Skal deploye organisasjoner en gang for to hoved organisasjoner")).deployOrganisasjon(any());
    }

    @Test
    public void should_run_orgnummer_save_exactly_two_times_for_two_hovedorganisasjoner() {

        organisasjonClient.opprett(bestilling, BESTILLING_ID);

        verify(organisasjonNummerService, times(2).description("Skal lagre orgnummer nøyaktig to ganger")).save(any());
    }

    @Test
    public void should_throw_dollyfunctionalerror_for_empty_orgnummer_response() {

        when(organisasjonConsumer.postOrganisasjon(any())).thenReturn(new ResponseEntity<>(null, HttpStatus.CREATED));

        Assertions.assertThrows(DollyFunctionalException.class, () ->
                organisasjonClient.opprett(bestilling, BESTILLING_ID));
    }

}
