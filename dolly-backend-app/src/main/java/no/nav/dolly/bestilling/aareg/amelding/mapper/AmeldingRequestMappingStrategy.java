package no.nav.dolly.bestilling.aareg.amelding.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.aareg.amelding.domain.Virksomhet;
import no.nav.dolly.domain.resultset.aareg.RsAmeldingRequest;
import no.nav.dolly.domain.resultset.aareg.RsAntallTimerIPerioden;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforholdAareg;
import no.nav.dolly.domain.resultset.aareg.RsFartoy;
import no.nav.dolly.domain.resultset.aareg.RsPermisjon;
import no.nav.dolly.domain.resultset.aareg.RsPermittering;
import no.nav.dolly.domain.resultset.aareg.RsUtenlandsopphold;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.AMeldingDTO;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.FartoeyDTO;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.InntektDTO;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.PermisjonDTO;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.PersonDTO;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.VirksomhetDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Component
public class AmeldingRequestMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsAmeldingRequest.class, AMeldingDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsAmeldingRequest rsAmelding,
                                        AMeldingDTO amelding, MappingContext context) {

                        String[] date = rsAmelding.getMaaned().split("-");
                        amelding.setKalendermaaned(LocalDate.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), 1));

                        List<Virksomhet> virksomheter = mapperFacade.mapAsList(rsAmelding.getArbeidsforhold(), Virksomhet.class);
                        List<VirksomhetDTO> ameldingVirksomheter = virksomheter.stream().map(virksomhet ->
                                VirksomhetDTO.builder()
                                        .organisajonsnummer(virksomhet.getOrganisajonsnummer())
                                        .personer(virksomhet.getPersoner())
                                        .build())
                                .collect(Collectors.toList());

                        amelding.setVirksomheter(ameldingVirksomheter);
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsArbeidsforholdAareg.class, Virksomhet.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsArbeidsforholdAareg rsArbeidsforholdAareg, Virksomhet virksomhet, MappingContext context) {

                        virksomhet.setOrganisajonsnummer(rsArbeidsforholdAareg.getArbeidsgiver().getOrgnummer());
                        virksomhet.setPersoner(List.of(PersonDTO.builder()
                                .ident((String) context.getProperty("personIdent"))
                                .arbeidsforhold(List.of(ArbeidsforholdDTO.builder()
                                        .antallTimerPerUke(
                                                !rsArbeidsforholdAareg.getAntallTimerForTimeloennet().isEmpty()
                                                        ? rsArbeidsforholdAareg.getAntallTimerForTimeloennet().get(0).getAntallTimer().floatValue()
                                                        : rsArbeidsforholdAareg.getArbeidsavtale().getAvtaltArbeidstimerPerUke().floatValue())
                                        .arbeidsforholdId(nonNull(rsArbeidsforholdAareg.getArbeidsforholdID()) ? rsArbeidsforholdAareg.getArbeidsforholdID() : "1")
                                        .arbeidsforholdType((String) context.getProperty("arbeidsforholdstype"))
                                        .arbeidstidsordning(rsArbeidsforholdAareg.getArbeidsavtale().getArbeidstidsordning())
                                        .fartoey(nonNull(rsArbeidsforholdAareg.getFartoy()) ? mapperFacade.map(rsArbeidsforholdAareg.getFartoy(), FartoeyDTO.class) : null)
                                        .inntekter(
                                                Stream.of(
                                                        mapperFacade.mapAsList(rsArbeidsforholdAareg.getAntallTimerForTimeloennet(), InntektDTO.class),
                                                        mapperFacade.mapAsList(rsArbeidsforholdAareg.getUtenlandsopphold(), InntektDTO.class))
                                                        .flatMap(Collection::stream).collect(Collectors.toList()))
                                        .yrke(rsArbeidsforholdAareg.getArbeidsavtale().getYrke())
                                        .arbeidstidsordning(rsArbeidsforholdAareg.getArbeidsavtale().getArbeidstidsordning())
                                        .stillingsprosent(nonNull(rsArbeidsforholdAareg.getArbeidsavtale().getStillingsprosent()) ? rsArbeidsforholdAareg.getArbeidsavtale().getStillingsprosent().floatValue() : null)
                                        .sisteLoennsendringsdato(nonNull(rsArbeidsforholdAareg.getArbeidsavtale().getEndringsdatoLoenn()) ? rsArbeidsforholdAareg.getArbeidsavtale().getEndringsdatoLoenn().toLocalDate() : null)
                                        .permisjoner(nonNull(rsArbeidsforholdAareg.getPermisjon()) ? mapperFacade.mapAsList(rsArbeidsforholdAareg.getPermisjon(), PermisjonDTO.class) : null)
                                        .build()))
                                .build()
                        ));

                        if (nonNull(rsArbeidsforholdAareg.getPermittering()) && !rsArbeidsforholdAareg.getPermittering().isEmpty()) {
                            virksomhet.getPersoner().get(0).getArbeidsforhold().get(0).getPermisjoner().addAll(
                                    mapperFacade.mapAsList(rsArbeidsforholdAareg.getPermittering(), PermisjonDTO.class)
                            );
                        }

                        if (nonNull(rsArbeidsforholdAareg.getUtenlandsopphold()) && !rsArbeidsforholdAareg.getUtenlandsopphold().isEmpty()) {
                            virksomhet.getPersoner().get(0).getArbeidsforhold().get(0).getInntekter().addAll(
                                    mapperFacade.mapAsList(rsArbeidsforholdAareg.getUtenlandsopphold(), InntektDTO.class)
                            );
                        }
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsFartoy.class, FartoeyDTO.class)
                .byDefault()
                .register();

        factory.classMap(RsPermisjon.class, PermisjonDTO.class)
                .byDefault()
                .register();

        factory.classMap(RsPermittering.class, PermisjonDTO.class).customize(new CustomMapper<>() {
            @Override
            public void mapAtoB(RsPermittering rsPermittering, PermisjonDTO permisjonDTO, MappingContext context) {
                permisjonDTO.setPermisjonId("permittering");
            }
        })
                .byDefault()
                .register();

        factory.classMap(RsUtenlandsopphold.class, InntektDTO.class).customize(new CustomMapper<>() {
            @Override
            public void mapAtoB(RsUtenlandsopphold utenlandsopphold, InntektDTO inntekt, MappingContext context) {
                inntekt.setStartdatoOpptjeningsperiode(nonNull(utenlandsopphold.getPeriode().getFom()) ? utenlandsopphold.getPeriode().getFom().toLocalDate() : null);
                inntekt.setSluttdatoOpptjeningsperiode(nonNull(utenlandsopphold.getPeriode().getTom()) ? utenlandsopphold.getPeriode().getTom().toLocalDate() : null);
                inntekt.setOpptjeningsland(utenlandsopphold.getLand());
            }
        })
                .byDefault()
                .register();

        factory.classMap(RsAntallTimerIPerioden.class, InntektDTO.class).customize(new CustomMapper<>() {
            @Override
            public void mapAtoB(RsAntallTimerIPerioden antallTimerIPerioden, InntektDTO inntekt, MappingContext context) {
                inntekt.setStartdatoOpptjeningsperiode(nonNull(antallTimerIPerioden.getPeriode().getFom()) ? antallTimerIPerioden.getPeriode().getFom().toLocalDate() : null);
                inntekt.setSluttdatoOpptjeningsperiode(nonNull(antallTimerIPerioden.getPeriode().getTom()) ? antallTimerIPerioden.getPeriode().getTom().toLocalDate() : null);
                inntekt.setAntall(antallTimerIPerioden.getAntallTimer().intValue());
            }
        })
                .byDefault()
                .register();
    }
}