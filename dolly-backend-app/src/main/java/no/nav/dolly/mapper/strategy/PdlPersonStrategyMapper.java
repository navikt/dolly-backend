package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.tpsf.InnvandretUtvandret;
import no.nav.dolly.domain.resultset.tpsf.InnvandretUtvandret.InnUtvandret;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Component
public final class PdlPersonStrategyMapper implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(PdlPersonBolk.PersonBolk.class, Person.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PdlPersonBolk.PersonBolk personBolk, Person person, MappingContext context) {

                        Optional<PdlPerson.Navn> navn = personBolk.getPerson().getNavn().stream()
                                .filter(personNavn -> !personNavn.getMetadata().isHistorisk())
                                .findFirst();
                        if (navn.isPresent()) {
                            mapperFacade.map(navn.get(), person);
                        }

                        person.setIdenttype(personBolk.getPerson().getFolkeregisteridentifikator().stream()
                                .filter(ident -> !ident.getMetadata().isHistorisk())
                                .map(PdlPerson.Folkeregisteridentifikator::getType)
                                .findFirst().orElse(null));

                        Optional<String> gender = personBolk.getPerson().getKjoenn().stream()
                                .filter(kjoenn -> !kjoenn.getMetadata().isHistorisk())
                                .map(PdlPerson.Kjoenn::getKjoenn)
                                .findFirst();
                        person.setKjonn(gender.isPresent() ? gender.get().substring(0, 1) : null);

                        Optional<LocalDate> foedselsdag = personBolk.getPerson().getFoedsel().stream()
                                .filter(foedsel -> !foedsel.getMetadata().isHistorisk())
                                .map(PdlPerson.Foedsel::getFoedselsdato)
                                .findFirst();
                        person.setFoedselsdato(foedselsdag.isPresent() ?
                                foedselsdag.get().atStartOfDay() : null);

                        Optional<LocalDate> doedsdato = personBolk.getPerson().getDoedsfall().stream()
                                .filter(doedsfall -> !doedsfall.getMetadata().isHistorisk())
                                .map(PdlPerson.Doedsfall::getDoedsdato)
                                .findFirst();
                        person.setDoedsdato(doedsdato.isPresent() ? doedsdato.get().atStartOfDay() : null);

                        Optional<PdlPerson.UtflyttingFraNorge> utvandret =
                                personBolk.getPerson().getUtflyttingFraNorge().stream()
                                        .filter(utflytting -> !utflytting.getMetadata().isHistorisk())
                                        .findFirst();
                        person.getInnvandretUtvandret().addAll(
                                utvandret.isPresent() ? singletonList(InnvandretUtvandret.builder()
                                        .innutvandret(InnUtvandret.UTVANDRET)
                                        .landkode(utvandret.get().getTilflyttingsland())
                                        .flyttedato(utvandret.get().getFolkeregistermetadata()
                                                .getGyldighetstidspunkt().atStartOfDay())
                                        .build()) : emptyList());
                    }
                })
                .byDefault()
                .register();
    }
}
