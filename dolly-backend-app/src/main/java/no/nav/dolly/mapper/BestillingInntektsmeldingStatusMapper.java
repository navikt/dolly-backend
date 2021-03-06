package no.nav.dolly.mapper;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.INNTKMELD;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.checkAndUpdateStatus;

import java.util.ArrayList;
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
public final class BestillingInntektsmeldingStatusMapper {

    public static List<RsStatusRapport> buildInntektsmeldingStatusMap(List<BestillingProgress> progressList) {

        //  status     miljø       ident
        Map<String, Map<String, Set<String>>> statusEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getInntektsmeldingStatus())) {
                List.of(progress.getInntektsmeldingStatus().split(",")).forEach(status -> {
                    String[] environErrMsg = status.split(":", 2);
                    String environ = environErrMsg[0];
                    String errMsg = environErrMsg.length > 1 ? environErrMsg[1].trim().replaceAll("&", ",") : "";
                    checkAndUpdateStatus(statusEnvIdents, progress.getIdent(), environ, errMsg);
                });
            }
        });

        return statusEnvIdents.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(INNTKMELD).navn(INNTKMELD.getBeskrivelse())
                        .statuser(statusEnvIdents.entrySet().stream().map(status -> RsStatusRapport.Status.builder()
                                .melding(status.getKey())
                                .detaljert(status.getValue().entrySet().stream().map(envIdent -> RsStatusRapport.Detaljert.builder()
                                        .miljo(envIdent.getKey())
                                        .identer(new ArrayList<>(envIdent.getValue()))
                                        .build())
                                        .collect(Collectors.toList()))
                                .build())
                                .collect(Collectors.toList()))
                        .build());
    }
}