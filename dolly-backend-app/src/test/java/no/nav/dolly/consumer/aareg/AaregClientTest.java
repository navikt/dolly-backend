package no.nav.dolly.consumer.aareg;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.aareg.AaregClient;
import no.nav.dolly.bestilling.aareg.AaregConsumer;
import no.nav.dolly.bestilling.aareg.domain.AaregOpprettRequest;
import no.nav.dolly.bestilling.aareg.domain.AaregResponse;
import no.nav.dolly.bestilling.aareg.domain.Aktoer;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregArbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;

@RunWith(MockitoJUnitRunner.class)
public class AaregClientTest {

    private static final String IDENT = "111111111111";
    private static final String ENV = "u2";
    private static final String ORGNUMMER = "222222222";

    @Mock
    private AaregConsumer aaregConsumer;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private AaregClient aaregClient;

    @Test
    public void gjenopprettArbeidsforhold_intetTidligereArbeidsforholdFinnes_OK() {
        Map<String, String> status = new HashMap<>();
        status.put(ENV, "OK");
        AaregResponse aaregResponse = AaregResponse.builder()
                .statusPerMiljoe(status)
                .build();

        when(mapperFacade.mapAsList(anyList(),eq(Arbeidsforhold.class))).thenReturn(asList(new Arbeidsforhold()));
        when(aaregConsumer.hentArbeidsforhold(IDENT, ENV)).thenReturn(emptyList());
        when(aaregConsumer.opprettArbeidsforhold(any(AaregOpprettRequest.class))).thenReturn(aaregResponse);

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsAaregArbeidsforhold.builder().build()));
        request.setEnvironments(singletonList("u2"));
        aaregClient.gjenopprett(request,
                DollyPerson.builder().hovedperson(IDENT).build(), new BestillingProgress(), false);

        verify(aaregConsumer).opprettArbeidsforhold(any(AaregOpprettRequest.class));
    }

    @Test
    public void gjenopprettArbeidsforhold_intetTidligereArbeidsforholdFinnes_lesKasterException() {
        Map<String, String> status = new HashMap<>();
        status.put(ENV, "OK");
        AaregResponse aaregResponse = AaregResponse.builder()
                .statusPerMiljoe(status)
                .build();

        when(mapperFacade.mapAsList(anyList(),eq(Arbeidsforhold.class))).thenReturn(asList(new Arbeidsforhold()));
        when(aaregConsumer.hentArbeidsforhold(IDENT, ENV)).thenReturn(emptyList());
        when(aaregConsumer.opprettArbeidsforhold(any(AaregOpprettRequest.class))).thenReturn(aaregResponse);

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsAaregArbeidsforhold.builder().build()));
        request.setEnvironments(singletonList("u2"));
        aaregClient.gjenopprett(request,
                DollyPerson.builder().hovedperson(IDENT).build(), new BestillingProgress(), false);

        verify(aaregConsumer).opprettArbeidsforhold(any(AaregOpprettRequest.class));
    }

    @Test
    public void gjenopprettArbeidsforhold_tidligereArbeidsforholdFinnes_arbeidsgiverHarOrgnummer() {
        Map<String, String> status = new HashMap<>();
        status.put(ENV, "OK");
        AaregResponse aaregResponse = AaregResponse.builder()
                .statusPerMiljoe(status)
                .build();

        when(aaregConsumer.hentArbeidsforhold(IDENT, ENV)).thenReturn(asList(buildArbeidsforhold(true)));

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsAaregArbeidsforhold.builder()
                .arbeidsgiver(RsOrganisasjon.builder().orgnummer(ORGNUMMER).build())
                .build()));
        request.setEnvironments(singletonList("u2"));
        aaregClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT).build(), new BestillingProgress(), false);
    }

    @Test
    public void gjenopprettArbeidsforhold_tidligereArbeidsforholdFinnes_arbeidsgiverHarPersonnr() {
        Map<String, String> status = new HashMap<>();
        status.put(ENV, "OK");
        AaregResponse aaregResponse = AaregResponse.builder()
                .statusPerMiljoe(status)
                .build();

        when(aaregConsumer.hentArbeidsforhold(IDENT, ENV)).thenReturn(asList(buildArbeidsforhold(false)));

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsAaregArbeidsforhold.builder()
                .arbeidsgiver(RsAktoerPerson.builder().ident(IDENT).build())
                .build()));
        request.setEnvironments(singletonList("u2"));
        aaregClient.gjenopprett(request,
                DollyPerson.builder().hovedperson(IDENT).build(), new BestillingProgress(), false);
    }

    @Test
    public void gjenopprettArbeidsforhold_tidligereArbeidsforholdFinnes_sjekkReturStatus() {

        when(aaregConsumer.hentArbeidsforhold(IDENT, ENV)).thenReturn(asList(buildArbeidsforhold(false)));
        Map<String, String> status = new HashMap<>();
        status.put(ENV, "OK");
        AaregResponse aaregResponse = AaregResponse.builder()
                .statusPerMiljoe(status)
                .build();

        BestillingProgress progress = new BestillingProgress();

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setAareg(singletonList(RsAaregArbeidsforhold.builder()
                .arbeidsgiver(RsAktoerPerson.builder().ident(IDENT).build())
                .build()));
        request.setEnvironments(singletonList("u2"));
        aaregClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT).build(), progress, false);

        assertThat(progress.getAaregStatus(), is(equalTo("u2: arbforhold=0$OK")));
    }

    private ArbeidsforholdResponse buildArbeidsforhold(boolean isOrgnummer) {

        return ArbeidsforholdResponse.builder()
                .arbeidstaker(ArbeidsforholdResponse.Arbeidstaker.builder()
                        .offentligIdent(IDENT)
                        .build())
                .arbeidsgiver(isOrgnummer ?
                        ArbeidsforholdResponse.Arbeidsgiver.builder()
                                .type(Aktoer.Organisasjon)
                                .organisasjonsnummer(ORGNUMMER)
                                .build() :
                        ArbeidsforholdResponse.Arbeidsgiver.builder()
                                .type(Aktoer.Person)
                                .offentligIdent(IDENT)
                                .build())
                .arbeidsavtaler(asList(ArbeidsforholdResponse.Arbeidsavtale.builder()
                        .yrke("121232")
                        .arbeidstidsordning("nada")
                        .build()))
                .arbeidsforholdId("1")
                .build();
    }
}