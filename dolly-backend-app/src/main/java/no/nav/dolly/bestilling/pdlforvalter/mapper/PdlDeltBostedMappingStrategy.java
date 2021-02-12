package no.nav.dolly.bestilling.pdlforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlDeltBosted;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlDeltBosted.PdlDelteBosteder;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlVegadresse;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Collectors;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Component
public class PdlDeltBostedMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlDelteBosteder.class)

                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlDelteBosteder deltBosted, MappingContext context) {

                        deltBosted.getDelteBosteder()
                                .addAll(person.getBoadresse().stream()
                                        .filter(boAdresse -> isTrue(boAdresse.getDeltAdresse()))
                                        .map(boAdresse -> PdlDeltBosted.builder()
                                                .vegadresse(mapperFacade.map(boAdresse, PdlVegadresse.class))
                                                .kilde(CONSUMER)
                                                .startdatoForKontrakt(LocalDate.now())
                                                .build())
                                        .collect(Collectors.toList()));
                    }
                })
                .register();
    }
}
