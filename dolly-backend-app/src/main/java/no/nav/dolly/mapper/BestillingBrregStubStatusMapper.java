package no.nav.dolly.mapper;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.BRREGSTUB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.postgres.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingBrregStubStatusMapper {

    public static List<RsStatusRapport> buildBrregStubStatusMap(List<BestillingProgress> progressList) {

        Map<String, List<String>> statusMap = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getBrregstubStatus())) {
                if (statusMap.containsKey(progress.getBrregstubStatus())) {
                    statusMap.get(progress.getBrregstubStatus()).add(progress.getIdent());
                } else {
                    statusMap.put(progress.getBrregstubStatus(), new ArrayList<>(List.of(progress.getIdent())));
                }
            }
        });

        return statusMap.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(BRREGSTUB).navn(BRREGSTUB.getBeskrivelse())
                        .statuser(statusMap.entrySet().stream()
                                .map(entry -> RsStatusRapport.Status.builder()
                                        .melding(entry.getKey().replaceAll("=", ":"))
                                        .identer(entry.getValue())
                                        .build())
                                .collect(Collectors.toList()))
                        .build());
    }
}
