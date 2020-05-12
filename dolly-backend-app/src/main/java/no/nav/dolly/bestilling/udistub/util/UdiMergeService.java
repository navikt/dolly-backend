package no.nav.dolly.bestilling.udistub.util;

import static java.util.Objects.isNull;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.bestilling.udistub.domain.RsAliasRequest;
import no.nav.dolly.bestilling.udistub.domain.RsAliasResponse;
import no.nav.dolly.bestilling.udistub.domain.UdiPerson;
import no.nav.dolly.bestilling.udistub.domain.UdiPerson.UdiAlias;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonWrapper;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonWrapper.Status;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiAlias;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;
import no.nav.dolly.domain.resultset.udistub.model.UdiPersonNavn;
import no.nav.dolly.service.TpsfPersonCache;

@Slf4j
@Service
@RequiredArgsConstructor
public class UdiMergeService {

    private final TpsfPersonCache tpsfPersonCache;
    private final TpsfService tpsfService;
    private final MapperFacade mapperFacade;

    public UdiPersonWrapper merge(RsUdiPerson nyUdiPerson, UdiPersonResponse eksisterendeUdiPerson,
            boolean isLeggTil, TpsPerson tpsPerson) {

        UdiPerson udiPerson = mapperFacade.map(nyUdiPerson, UdiPerson.class);

        if (isNull(eksisterendeUdiPerson)) {
            return appendAttributes(udiPerson, nyUdiPerson.getAliaser(), Status.NEW, tpsPerson);
        }

        udiPerson.setAvgjoerelser(udiPerson.getAvgjoerelser().stream()
                .filter(nyAvgjoerelse -> eksisterendeUdiPerson.getPerson().getAvgjoerelser().stream()
                .noneMatch(eksisterendeAvgjoerelse -> eksisterendeAvgjoerelse.equals(nyAvgjoerelse)))
                .collect(Collectors.toList()));

        return appendAttributes(udiPerson, isLeggTil ? nyUdiPerson.getAliaser() : null, Status.UPDATE, tpsPerson);
    }

    public List<UdiAlias> getAliaser(RsAliasRequest request, List<String> environments) {

        request.setEnvironments(environments);
        ResponseEntity<RsAliasResponse> response = tpsfService.createAliases(request);
        return response.hasBody() ? mapperFacade.mapAsList(tpsfService.createAliases(request).getBody().getAliaser(), UdiAlias.class) : null;
    }

    private UdiPersonWrapper appendAttributes(UdiPerson udiPerson, List<RsUdiAlias> aliaser, Status status, TpsPerson tpsPerson) {

        tpsfPersonCache.fetchIfEmpty(tpsPerson);

        udiPerson.setIdent(tpsPerson.getHovedperson());
        udiPerson.setNavn(mapperFacade.map(tpsPerson.getPerson(tpsPerson.getHovedperson()), UdiPersonNavn.class));
        udiPerson.setFoedselsDato(tpsPerson.getPerson(tpsPerson.getHovedperson()).getFoedselsdato().toLocalDate());

        return UdiPersonWrapper.builder()
                .udiPerson(udiPerson)
                .status(status)
                .aliasRequest(!aliaser.isEmpty() ? RsAliasRequest.builder()
                                .ident(tpsPerson.getHovedperson())
                                .aliaser(mapperFacade.mapAsList(aliaser, RsAliasRequest.AliasSpesification.class))
                                .build() : null)
                .build();
    }
}
