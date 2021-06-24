package no.nav.dolly.bestilling.aareg.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsAareg.RsAaregArbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsavtale;
import no.nav.dolly.domain.resultset.aareg.RsFartoy;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.aareg.RsPeriodeAareg;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.PermisjonDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Component
public class AaregRequestMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsAaregArbeidsforhold.class, Arbeidsforhold.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsAaregArbeidsforhold rsArbeidsforhold,
                                        Arbeidsforhold arbeidsforhold, MappingContext context) {

                        arbeidsforhold.setArbeidsforholdID(rsArbeidsforhold.getArbeidsforholdId());

                        if (nonNull(rsArbeidsforhold.getAntallTimerForTimeloennet()) && !rsArbeidsforhold.getAntallTimerForTimeloennet().isEmpty()) {
                            arbeidsforhold.setAntallTimerForTimeloennet(rsArbeidsforhold.getAntallTimerForTimeloennet());
                        }

                        arbeidsforhold.setArbeidsavtale(RsArbeidsavtale.builder()
                                .antallKonverterteTimer(rsArbeidsforhold.getArbeidsavtale().getAntallKonverterteTimer())
                                .arbeidstidsordning(rsArbeidsforhold.getArbeidsavtale().getArbeidstidsordning())
                                .avloenningstype(rsArbeidsforhold.getArbeidsavtale().getAvloenningstype())
                                .avtaltArbeidstimerPerUke(rsArbeidsforhold.getArbeidsavtale().getAvtaltArbeidstimerPerUke())
                                .endringsdatoStillingsprosent(rsArbeidsforhold.getArbeidsavtale().getEndringsdatoStillingsprosent())
                                .sisteLoennsendringsdato(rsArbeidsforhold.getArbeidsavtale().getSisteLoennsendringsdato())
                                .stillingsprosent(rsArbeidsforhold.getArbeidsavtale().getStillingsprosent())
                                .yrke(rsArbeidsforhold.getArbeidsavtale().getYrke())
                                .endringsdatoLoenn(rsArbeidsforhold.getArbeidsavtale().getEndringsdatoLoenn())
                                .ansettelsesform(rsArbeidsforhold.getArbeidsavtale().getAnsettelsesform())
                                .build());

                        if (nonNull(rsArbeidsforhold.getFartoy()) && !rsArbeidsforhold.getFartoy().isEmpty()) {
                            arbeidsforhold.setFartoy(Collections.singletonList(RsFartoy.builder()
                                    .fartsomraade(rsArbeidsforhold.getFartoy().get(0).getFartsomraade())
                                    .skipsregister(rsArbeidsforhold.getFartoy().get(0).getSkipsregister())
                                    .skipstype(rsArbeidsforhold.getFartoy().get(0).getSkipstype())
                                    .build()));
                        }

                        if (nonNull(rsArbeidsforhold.getUtenlandsopphold()) && !rsArbeidsforhold.getUtenlandsopphold().isEmpty()) {
                            arbeidsforhold.setUtenlandsopphold(rsArbeidsforhold.getUtenlandsopphold());
                        }

                        if (rsArbeidsforhold.getArbeidsgiver() instanceof RsOrganisasjon) {
                            arbeidsforhold.setArbeidsgiver(RsOrganisasjon.builder()
                                    .orgnummer(((RsOrganisasjon) rsArbeidsforhold.getArbeidsgiver()).getOrgnummer())
                                    .build());
                            arbeidsforhold.getArbeidsgiver().setAktoertype("ORG");
                        } else if (rsArbeidsforhold.getArbeidsgiver() instanceof RsAktoerPerson) {
                            arbeidsforhold.setArbeidsgiver(RsAktoerPerson.builder()
                                    .ident(((RsAktoerPerson) rsArbeidsforhold.getArbeidsgiver()).getIdent())
                                    .identtype(((RsAktoerPerson) rsArbeidsforhold.getArbeidsgiver()).getIdenttype())
                                    .build());
                            arbeidsforhold.getArbeidsgiver().setAktoertype("PERS");
                        }
                        if (nonNull(rsArbeidsforhold.getAnsettelsesPeriode())) {
                            arbeidsforhold.setAnsettelsesPeriode(RsPeriodeAareg.builder()
                                    .fom(rsArbeidsforhold.getAnsettelsesPeriode().getFom())
                                    .tom(rsArbeidsforhold.getAnsettelsesPeriode().getTom())
                                    .periode(rsArbeidsforhold.getAnsettelsesPeriode().getPeriode())
                                    .build());
                        }
                        arbeidsforhold.setArbeidsforholdstype((String) context.getProperty("arbeidsforholdstype"));

                        arbeidsforhold.setPermisjon((nonNull(rsArbeidsforhold.getPermisjon()) && !rsArbeidsforhold.getPermisjon().isEmpty())
                                || (nonNull(rsArbeidsforhold.getPermittering()) && !rsArbeidsforhold.getPermittering().isEmpty())
                                ? Stream.concat(
                                mapperFacade.mapAsList(rsArbeidsforhold.getPermisjon(), PermisjonDTO.class).stream(),
                                mapperFacade.mapAsList(rsArbeidsforhold.getPermittering(), PermisjonDTO.class).stream())
                                .collect(Collectors.toList())
                                : null);
                    }
                })
                .register();
    }

}