package no.nav.dolly.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.Relasjon;
import no.nav.dolly.domain.resultset.tpsf.RsFullmakt;
import no.nav.dolly.domain.resultset.tpsf.RsOppdaterPersonResponse;
import no.nav.dolly.domain.resultset.tpsf.RsSimplePerson;
import no.nav.dolly.domain.resultset.tpsf.RsVergemaal;
import no.nav.dolly.domain.resultset.tpsf.adresse.IdentHistorikk;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class DollyPersonCache {

    private final TpsfService tpsfService;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final ObjectMapper objectMapper;
    private final MapperFacade mapperFacade;

    @SneakyThrows
    public DollyPerson fetchIfEmpty(DollyPerson dollyPerson) {

        Set<String> tpsfIdenter = new HashSet<>();
        Stream.of(singletonList(dollyPerson.getHovedperson()), dollyPerson.getPartnere(),
                dollyPerson.getBarn(), dollyPerson.getVerger(), dollyPerson.getIdenthistorikk())
                .forEach(tpsfIdenter::addAll);

        List<String> manglendeIdenter = tpsfIdenter.stream().filter(ident -> dollyPerson.getPersondetaljer().stream()
                .noneMatch(person -> person.getIdent().equals(ident)))
                .collect(Collectors.toList());

        if (!manglendeIdenter.isEmpty()) {
            if (dollyPerson.isTpsfMaster()) {
                dollyPerson.getPersondetaljer().addAll(tpsfService.hentTestpersoner(manglendeIdenter));
            } else {
                PdlPersonBolk pdlPersonBolk = objectMapper.readValue(
                        pdlPersonConsumer.getPdlPersoner(manglendeIdenter).toString(),
                        PdlPersonBolk.class);
                dollyPerson.getPersondetaljer().addAll(mapperFacade.mapAsList(pdlPersonBolk.getData().getHentPersonBolk(), Person.class));
            }
        }

        List<String> historikkIdenter = dollyPerson.getPerson(dollyPerson.getHovedperson()).getIdentHistorikk().stream()
                .map(IdentHistorikk::getAliasPerson)
                .filter(person -> dollyPerson.getPersondetaljer().stream()
                        .noneMatch(person1 -> person.getIdent().equals(person1.getIdent())))
                .map(Person::getIdent)
                .collect(Collectors.toList());

        if (!historikkIdenter.isEmpty()) {
            List<Person> historiskeidenter = tpsfService.hentTestpersoner(historikkIdenter);
            historiskeidenter.forEach(person -> dollyPerson.getPersondetaljer().add(0, person));
        }

        List<String> vergeIdenter = dollyPerson.getPersondetaljer().stream()
                .filter(person -> !person.getVergemaal().isEmpty() && person.getVergemaal().stream()
                        .noneMatch(vergemaal -> dollyPerson.getPersondetaljer().stream()
                                .anyMatch(person1 -> vergemaal.getVerge().getIdent().equals(person1.getIdent()))))
                .map(Person::getVergemaal)
                .flatMap(vergemaal -> vergemaal.stream().map(RsVergemaal::getVerge))
                .map(RsSimplePerson::getIdent)
                .collect(Collectors.toList());

        if (!vergeIdenter.isEmpty()) {
            dollyPerson.getPersondetaljer().addAll(tpsfService.hentTestpersoner(vergeIdenter));
        }

        List<String> fullmaktIdenter = dollyPerson.getPersondetaljer().stream()
                .filter(person -> !person.getFullmakt().isEmpty() && person.getFullmakt().stream()
                        .noneMatch(fullmakt -> dollyPerson.getPersondetaljer().stream()
                                .anyMatch(person1 -> fullmakt.getFullmektig().getIdent().equals(person1.getIdent()))))
                .map(Person::getFullmakt)
                .flatMap(fullmakt -> fullmakt.stream().map(RsFullmakt::getFullmektig))
                .map(RsSimplePerson::getIdent)
                .collect(Collectors.toList());

        if (!fullmaktIdenter.isEmpty()) {
            dollyPerson.getPersondetaljer().addAll(tpsfService.hentTestpersoner(fullmaktIdenter));
        }

        return dollyPerson;
    }

    public DollyPerson prepareTpsPersoner(RsOppdaterPersonResponse identer) {

        List<Person> personer = tpsfService.hentTestpersoner(identer.getIdentTupler().stream()
                .map(RsOppdaterPersonResponse.IdentTuple::getIdent)
                .collect(Collectors.toList()));

        if (!personer.isEmpty()) {
            return DollyPerson.builder()
                    .persondetaljer(personer)
                    .hovedperson(identer.getIdentTupler().get(0).getIdent())
                    .partnere(personer.get(0).getRelasjoner().stream()
                            .filter(Relasjon::isPartner)
                            .map(Relasjon::getPersonRelasjonMed)
                            .map(Person::getIdent)
                            .collect(Collectors.toList()))
                    .barn(personer.get(0).getRelasjoner().stream()
                            .filter(Relasjon::isBarn)
                            .map(Relasjon::getPersonRelasjonMed)
                            .map(Person::getIdent)
                            .collect(Collectors.toList()))
                    .nyePartnereOgBarn(identer.getIdentTupler().stream()
                            .filter(RsOppdaterPersonResponse.IdentTuple::isLagtTil)
                            .map(RsOppdaterPersonResponse.IdentTuple::getIdent)
                            .collect(Collectors.toList()))
                    .verger(personer.get(0).getVergemaal().stream()
                            .map(RsVergemaal::getVerge)
                            .map(RsSimplePerson::getIdent)
                            .collect(Collectors.toList()))
                    .fullmektige(personer.get(0).getFullmakt().stream()
                            .map(RsFullmakt::getFullmektig)
                            .map(RsSimplePerson::getIdent)
                            .collect(Collectors.toList()))
                    .identhistorikk(personer.get(0).getIdentHistorikk().stream()
                            .map(IdentHistorikk::getAliasPerson)
                            .map(Person::getIdent)
                            .collect(Collectors.toList()))
                    .master(Testident.Master.TPSF)
                    .build();
        }

        return new DollyPerson();
    }

    public DollyPerson prepareTpsPersoner(Person person) {

        return fetchIfEmpty(DollyPerson.builder()
                .hovedperson(person.getIdent())
                .partnere(person.getRelasjoner().stream()
                        .filter(Relasjon::isPartner)
                        .map(Relasjon::getPersonRelasjonMed)
                        .map(Person::getIdent)
                        .collect(Collectors.toList()))
                .barn(person.getRelasjoner().stream()
                        .filter(Relasjon::isBarn)
                        .map(Relasjon::getPersonRelasjonMed)
                        .map(Person::getIdent)
                        .collect(Collectors.toList()))
                .verger(person.getVergemaal().stream()
                        .map(RsVergemaal::getVerge)
                        .map(RsSimplePerson::getIdent)
                        .collect(Collectors.toList()))
                .fullmektige(person.getFullmakt().stream()
                        .map(RsFullmakt::getFullmektig)
                        .map(RsSimplePerson::getIdent)
                        .collect(Collectors.toList()))
                .identhistorikk(person.getIdentHistorikk().stream()
                        .map(IdentHistorikk::getAliasPerson)
                        .map(Person::getIdent)
                        .collect(Collectors.toList()))
                .master(Testident.Master.TPSF)
                .build());
    }

    public DollyPerson preparePdlPersoner(PdlPerson pdlPerson) {

        return DollyPerson.builder()
                .hovedperson(pdlPerson.getData().getHentPerson().getFolkeregisteridentifikator().stream()
                        .filter(ident -> !ident.getMetadata().isHistorisk())
                        .map(PdlPerson.Folkeregisteridentifikator::getIdentifikasjonsnummer)
                        .findFirst().get())
                .partnere(pdlPerson.getData().getHentPerson().getSivilstand().stream()
                        .filter(sivilstand -> !sivilstand.getMetadata().isHistorisk() &&
                                nonNull(sivilstand.getRelatertVedSivilstand()))
                        .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                        .collect(Collectors.toList()))
                .barn(pdlPerson.getData().getHentPerson().getForelderBarnRelasjon().stream()
                        .filter(relasjon -> !relasjon.getMetadata().isHistorisk() &&
                                relasjon.getRelatertPersonsRolle() == PdlPerson.Rolle.BARN)
                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                        .collect(Collectors.toList()))
                .identhistorikk(pdlPerson.getData().getHentPerson().getFolkeregisteridentifikator().stream()
                        .filter(ident -> ident.getMetadata().isHistorisk())
                        .map(PdlPerson.Folkeregisteridentifikator::getIdentifikasjonsnummer)
                        .collect(Collectors.toList()))
                .master(Testident.Master.PDL)
                .build();
    }
}
