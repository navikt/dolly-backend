package no.nav.dolly.bestilling.inntektstub.mapper;

import static java.util.Objects.isNull;
import static no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon.builder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.inntektstub.domain.Inntekt;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.bestilling.inntektstub.domain.InntektsinformasjonWrapper;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class InntektsinformasjonMappingStrategy implements MappingStrategy {

    private static final DateTimeFormatter YEAR_MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(InntektMultiplierWrapper.class, InntektsinformasjonWrapper.class)
                .customize(new CustomMapper<InntektMultiplierWrapper, InntektsinformasjonWrapper>() {
                    @Override
                    public void mapAtoB(InntektMultiplierWrapper inntektMultiplierWrapper, InntektsinformasjonWrapper inntektsinformasjonWrapper, MappingContext context) {

                        inntektMultiplierWrapper.getInntektsinformasjon().forEach(inntektsinformasjon -> {

                            AtomicReference<LocalDate> yearMonth =
                                    new AtomicReference<>(LocalDate.parse(inntektsinformasjon.getSisteAarMaaned() + "-01"));
                            int antallMaaneder = isNull(inntektsinformasjon.getAntallMaaneder()) || inntektsinformasjon.getAntallMaaneder() < 0 ? 1 :
                                    inntektsinformasjon.getAntallMaaneder();

                            do {
                                Inntektsinformasjon inntektsinformasjon1 = mapperFacade.map(
                                        inntektsinformasjon, Inntektsinformasjon.class);

                                inntektsinformasjon1.setAarMaaned(yearMonth.get().format(YEAR_MONTH_FORMAT));

                                inntektsinformasjonWrapper.getInntektsinformasjon().add(inntektsinformasjon1);

                                AtomicInteger versjon = new AtomicInteger(0);
                                inntektsinformasjon.getHistorikk().forEach(historikk ->

                                    inntektsinformasjonWrapper.getInntektsinformasjon().add(builder()
                                            .aarMaaned(yearMonth.get().format(YEAR_MONTH_FORMAT))
                                            .opplysningspliktig(inntektsinformasjon.getOpplysningspliktig())
                                            .virksomhet(inntektsinformasjon.getVirksomhet())
                                            .inntektsliste(mapperFacade.mapAsList(historikk.getInntektsliste(), Inntekt.class))
                                            .fradragsliste(mapperFacade.mapAsList(historikk.getFradragsliste(), Inntektsinformasjon.Fradrag.class))
                                            .forskuddstrekksliste(mapperFacade.mapAsList(historikk.getForskuddstrekksliste(), Inntektsinformasjon.Forskuddstrekk.class))
                                            .arbeidsforholdsliste(mapperFacade.mapAsList(historikk.getArbeidsforholdsliste(), Inntektsinformasjon.Arbeidsforhold.class))
                                            .versjon(versjon.addAndGet(1))
                                            .build())
                                );

                                yearMonth.updateAndGet(ym -> ym.minusMonths(1));
                            } while (--antallMaaneder > 0);

                        });
                    }
                })
                .register();
    }
}
