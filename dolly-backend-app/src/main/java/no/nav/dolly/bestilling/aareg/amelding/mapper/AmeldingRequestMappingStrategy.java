package no.nav.dolly.bestilling.aareg.amelding.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.aareg.RsAmeldingRequest;
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
import java.util.List;

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

                        String[] date = rsAmelding.getMaaned().split(",");
                        amelding.setKalendermaaned(LocalDate.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), 1));
                        amelding.setVirksomheter(mapperFacade.mapAsList(rsAmelding.getRsArbeidsforholdAareg(), VirksomhetDTO.class));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsArbeidsforholdAareg.class, VirksomhetDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsArbeidsforholdAareg rsArbeidsforholdAareg, VirksomhetDTO virksomhetDTO, MappingContext context) {

                        virksomhetDTO = VirksomhetDTO.builder()
                                .organisajonsnummer(rsArbeidsforholdAareg.getArbeidsgiver().getOrgnummer())
                                .personer(List.of(PersonDTO.builder()
                                        .ident((String) context.getProperty("personIdent"))
                                        .arbeidsforhold(List.of(ArbeidsforholdDTO.builder()
                                                .antallTimerPerUke(rsArbeidsforholdAareg.getAntallTimerForTimeloennet().get(0).getAntallTimer().floatValue())
                                                .arbeidsforholdId(rsArbeidsforholdAareg.getArbeidsforholdID())
                                                .arbeidsforholdType(rsArbeidsforholdAareg.getArbeidsforholdstype())
                                                .arbeidstidsordning(rsArbeidsforholdAareg.getArbeidsavtale().getArbeidstidsordning())
                                                .fartoey(nonNull(rsArbeidsforholdAareg.getFartoy()) ? mapperFacade.map(rsArbeidsforholdAareg.getFartoy(), FartoeyDTO.class) : null)
                                                .inntekter(List.of(InntektDTO.builder()
                                                        .antall(rsArbeidsforholdAareg.getArbeidsavtale().getAvtaltArbeidstimerPerUke().intValue())
                                                        .opptjeningsland("NO")
                                                        .startdatoOpptjeningsperiode(nonNull(rsArbeidsforholdAareg.getAnsettelsesPeriode().getFom()) ? rsArbeidsforholdAareg.getAnsettelsesPeriode().getFom().toLocalDate() : null)
                                                        .sluttdatoOpptjeningsperiode(nonNull(rsArbeidsforholdAareg.getAnsettelsesPeriode().getTom()) ? rsArbeidsforholdAareg.getAnsettelsesPeriode().getFom().toLocalDate() : null)
                                                        .build()))
                                                .yrke(rsArbeidsforholdAareg.getArbeidsavtale().getYrke())
                                                .arbeidstidsordning(rsArbeidsforholdAareg.getArbeidsavtale().getArbeidstidsordning())
                                                .stillingsprosent(nonNull(rsArbeidsforholdAareg.getArbeidsavtale().getStillingsprosent()) ? rsArbeidsforholdAareg.getArbeidsavtale().getStillingsprosent().floatValue() : null)
                                                .sisteLoennsendringsdato(nonNull(rsArbeidsforholdAareg.getArbeidsavtale().getEndringsdatoLoenn()) ? rsArbeidsforholdAareg.getArbeidsavtale().getEndringsdatoLoenn().toLocalDate() : null)
                                                .permisjoner(nonNull(rsArbeidsforholdAareg.getPermisjon()) ? mapperFacade.mapAsList(rsArbeidsforholdAareg.getPermisjon(), PermisjonDTO.class) : null)
                                                .build()))
                                        .build()
                                ))
                                .build();

                        if (nonNull(rsArbeidsforholdAareg.getPermittering())) {
                            virksomhetDTO.getPersoner().get(0).getArbeidsforhold().get(0).getPermisjoner().addAll(
                                    mapperFacade.mapAsList(rsArbeidsforholdAareg.getPermittering(), PermisjonDTO.class)
                            );
                        }

                        if (nonNull(rsArbeidsforholdAareg.getUtenlandsopphold())) {
                            virksomhetDTO.getPersoner().get(0).getArbeidsforhold().get(0).getInntekter().addAll(
                                    mapperFacade.mapAsList(rsArbeidsforholdAareg.getUtenlandsopphold(), InntektDTO.class)
                            );
                        }
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsFartoy.class, FartoeyDTO.class).byDefault().register();
        factory.classMap(RsPermisjon.class, PermisjonDTO.class).byDefault().register();
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
    }
}