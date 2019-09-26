package no.nav.dolly.mapper;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.AAREG;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForholdNy.checkAndUpdateStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingAaregStatusMapperNy {

    public static List<RsStatusRapport> buildAaregStatusMap(List<BestillingProgress> progressList) {
        //  status     miljø       ident
        Map<String, Map<String, Set<String>>> errorEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getAaregStatus())) {
                newArrayList(progress.getAaregStatus().split(",")).forEach(status -> {
                    String[] environErrMsg = status.split(":");
                    String environ = environErrMsg[0];
                    String errMsg = environErrMsg.length > 1 ? environErrMsg[1].trim() : "";
                    checkAndUpdateStatus(errorEnvIdents, progress.getIdent(), environ, errMsg);
                });
            }
        });

        return errorEnvIdents.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(AAREG).navn(AAREG.getBeskrivelse())
                        .statuser(errorEnvIdents.entrySet().stream().map(status ->
                                RsStatusRapport.Status.builder()
                                        .melding(status.getKey())
                                        .detaljert(status.getValue().entrySet().stream().map(miljo ->
                                                RsStatusRapport.Detaljert.builder()
                                                        .miljo(miljo.getKey())
                                                        .identer(newArrayList(miljo.getValue()))
                                                        .build())
                                                .collect(Collectors.toList()))
                                        .build())
                                .collect(Collectors.toList()))
                        .build());
    }
}
