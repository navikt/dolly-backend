package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukerUtenServicebehov;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaAap;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaAap115;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukertype.UTEN_SERVICEBEHOV;
import static no.nav.dolly.domain.resultset.arenaforvalter.ArenaKvalifiseringsgruppe.IKVAL;

@Component
public class ArenaMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Arenadata.class, ArenaNyBruker.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Arenadata arenadata, ArenaNyBruker arenaNyBruker, MappingContext context) {

                        if (UTEN_SERVICEBEHOV.equals(arenadata.getArenaBrukertype())) {
                            arenaNyBruker.setUtenServicebehov(new ArenaBrukerUtenServicebehov());

                            arenaNyBruker.setKvalifiseringsgruppe(IKVAL);
                            if (nonNull(arenadata.getInaktiveringDato())) {
                                arenaNyBruker.getUtenServicebehov().setStansDato(arenadata.getInaktiveringDato().toLocalDate());
                            }
                        } else if (!arenadata.getAap().isEmpty() || !arenadata.getAap115().isEmpty()) {
                            arenaNyBruker.setAktiveringsDato(
                                    Stream.of(
                                            arenadata.getAap().stream()
                                                    .filter(Objects::nonNull)
                                                    .map(RsArenaAap::getFraDato),
                                            arenadata.getAap115().stream()
                                                    .filter(Objects::nonNull)
                                                    .map(RsArenaAap115::getFraDato))
                                            .flatMap(Stream::distinct)
                                            .map(LocalDateTime::toLocalDate)
                                            .min(LocalDate::compareTo)
                                            .orElse(null));
                        }
                    }
                })
                .byDefault()
                .register();
    }
}