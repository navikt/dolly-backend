package no.nav.dolly.bestilling.pdlforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlBostedadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlBostedsadresseHistorikk;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlMatrikkeladresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlVegadresse;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static no.nav.dolly.bestilling.pdlforvalter.mapper.PdlAdresseMappingStrategy.getCoadresse;
import static no.nav.dolly.bestilling.pdlforvalter.mapper.PdlAdresseMappingStrategy.getDato;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;

@Component
public class PdlBostedsadresseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlBostedsadresseHistorikk.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlBostedsadresseHistorikk historikk, MappingContext context) {

                        historikk.getPdlAdresser().addAll(
                                person.getBoadresse().stream()
                                        .filter(boAdresse -> isNotTrue(boAdresse.getDeltAdresse()))
                                        .map(boAdresse -> {
                                                    PdlBostedadresse bostedadresse = new PdlBostedadresse();
                                                    bostedadresse.setKilde(CONSUMER);
                                                    bostedadresse.setGyldigFraOgMed(getDato(boAdresse.getFlyttedato()));
                                                    bostedadresse.setCoAdressenavn(getCoadresse(boAdresse));
                                                    if (person.isUtenFastBopel()) {
                                                        bostedadresse.setUkjentBosted(PdlBostedadresse.UkjentBosted.builder()
                                                                .bostedskommune(boAdresse.getKommunenr())
                                                                .build());
                                                    } else {
                                                        if ("GATE".equals(boAdresse.getAdressetype())) {
                                                            bostedadresse.setVegadresse(mapperFacade.map(
                                                                    boAdresse, PdlVegadresse.class));
                                                        } else {
                                                            bostedadresse.setMatrikkeladresse(mapperFacade.map(
                                                                    boAdresse, PdlMatrikkeladresse.class));
                                                        }
                                                    }
                                                    return bostedadresse;
                                                }
                                        )
                                        .collect(Collectors.toList())
                        );
                    }
                })
                .register();
    }
}
