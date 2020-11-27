package no.nav.dolly.bestilling.pdlforvalter.mapper;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFullmakt;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFullmaktHistorikk;
import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

@Component
@RequiredArgsConstructor
public class PdlFullmaktMappingStrategy implements MappingStrategy {

    private static final String TEMA_KODEVERK = "TEMA";
    private final KodeverkConsumer kodeverkConsumer;

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlFullmaktHistorikk.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlFullmaktHistorikk historikk, MappingContext context) {

                        person.getFullmakt().forEach(fullmakt -> {

                            Map<String, String> temaKodeverk = kodeverkConsumer.getKodeverkByName(TEMA_KODEVERK);
                            PdlFullmakt pdlFullmakt = PdlFullmakt.builder()
                                    .fullmektig(fullmakt.getFullmektig().getIdent())
                                    .kilde(CONSUMER)
                                    .omraader(fullmakt.getOmraader().stream().map(temaKodeverk::get).collect(Collectors.toList()))
                                    .gyldigFom(fullmakt.getGyldigFom())
                                    .gyldigTom(fullmakt.getGyldigTom())
                                    .build();

                            historikk.getFullmakter().add(pdlFullmakt);
                        });
                    }
                })
                .register();
    }
}