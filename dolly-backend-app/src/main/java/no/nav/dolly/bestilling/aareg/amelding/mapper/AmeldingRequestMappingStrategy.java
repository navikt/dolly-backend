package no.nav.dolly.bestilling.aareg.amelding.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.aareg.RsAmeldingRequest;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsFartoy;
import no.nav.dolly.domain.resultset.aareg.RsPermisjon;
import no.nav.dolly.domain.resultset.aareg.RsPermittering;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.AMeldingDTO;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.FartoeyDTO;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.InntektDTO;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.PermisjonDTO;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.PersonDTO;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.VirksomhetDTO;
import org.springframework.stereotype.Component;

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

                        amelding.setKalendermaaned(rsAmelding.getMaaned());
                        List<VirksomhetDTO> virksomheter = mapperFacade.mapAsList(rsAmelding.getRsArbeidsforhold(), VirksomhetDTO.class);

                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsArbeidsforhold.class, VirksomhetDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsArbeidsforhold rsArbeidsforhold, VirksomhetDTO virksomhetDTO, MappingContext context) {
                        virksomhetDTO = VirksomhetDTO.builder()
                                .organisajonsnummer(rsArbeidsforhold.getArbeidsgiver().getOrgnummer())
                                .personer(List.of(PersonDTO.builder()
                                        .arbeidsforhold(List.of(ArbeidsforholdDTO.builder()
                                                .antallTimerPerUke(rsArbeidsforhold.getAntallTimerForTimeloennet().get(0).getAntallTimer().floatValue())
                                                .arbeidsforholdId(rsArbeidsforhold.getArbeidsforholdID())
                                                .arbeidsforholdType(rsArbeidsforhold.getArbeidsforholdstype())
                                                .arbeidstidsordning(rsArbeidsforhold.getArbeidsavtale().getArbeidstidsordning())
                                                .fartoey(mapperFacade.map(rsArbeidsforhold.getFartoy(), FartoeyDTO.class))
                                                .inntekter(List.of(InntektDTO.builder()
                                                        .antall(rsArbeidsforhold.getArbeidsavtale().getAvtaltArbeidstimerPerUke().intValue())
                                                        .opptjeningsland("NO")
                                                        .startdatoOpptjeningsperiode(nonNull(rsArbeidsforhold.getAnsettelsesPeriode().getFom()) ? rsArbeidsforhold.getAnsettelsesPeriode().getFom().toLocalDate() : null)
                                                        .sluttdatoOpptjeningsperiode(nonNull(rsArbeidsforhold.getAnsettelsesPeriode().getTom()) ? rsArbeidsforhold.getAnsettelsesPeriode().getFom().toLocalDate() : null)
                                                        .build()))
                                                .yrke(rsArbeidsforhold.getArbeidsavtale().getYrke())
                                                .arbeidstidsordning(rsArbeidsforhold.getArbeidsavtale().getArbeidstidsordning())
                                                .stillingsprosent(nonNull(rsArbeidsforhold.getArbeidsavtale().getStillingsprosent()) ? rsArbeidsforhold.getArbeidsavtale().getStillingsprosent().floatValue() : null)
                                                .sisteLoennsendringsdato(nonNull(rsArbeidsforhold.getArbeidsavtale().getEndringsdatoLoenn()) ? rsArbeidsforhold.getArbeidsavtale().getEndringsdatoLoenn().toLocalDate() : null)
                                                .permisjoner(mapperFacade.mapAsList(rsArbeidsforhold.getPermisjon(), PermisjonDTO.class))
                                                .build()))
                                        .build()
                                ))
                                .build();

                        if (nonNull(rsArbeidsforhold.getPermittering()) && !rsArbeidsforhold.getPermittering().isEmpty()) {
                            virksomhetDTO.getPersoner().get(0).getArbeidsforhold().get(0).getPermisjoner().addAll(
                                    mapperFacade.mapAsList(rsArbeidsforhold.getPermittering(), PermisjonDTO.class)
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
    }
}