package no.nav.dolly.mapper;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.TPSF;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.RsStatusRapport;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingTpsfStatusMapper {

    private static final String SUCCESS = "OK";

    public static List<RsStatusRapport> buildTpsfStatusMap(Bestilling bestilling) {

        Map<String, Map<String, Set<String>>> errorEnvIdents = new HashMap<>();

        if (isBlank(bestilling.getTpsImport())) {
            bestilling.getProgresser().forEach(progress -> {
                if (nonNull(progress.getTpsfSuccessEnv())) {
                    newArrayList(progress.getTpsfSuccessEnv().split(",")).forEach(environ ->
                            checkNUpdateStatus(errorEnvIdents, progress.getIdent(), environ, SUCCESS)
                    );
                }
                if (nonNull(progress.getFeil())) {
                    newArrayList(progress.getFeil().split(",")).forEach(error -> {
                        String[] environErrMsg = error.split(":", 2);
                        String environ = environErrMsg[0];
                        String errMsg = environErrMsg.length > 1 ? environErrMsg[1].trim() : "";
                        checkNUpdateStatus(errorEnvIdents, progress.getIdent(), environ, errMsg);
                    });
                }
            });
        }
        return errorEnvIdents.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(TPSF).navn(TPSF.getBeskrivelse())
                        .statuser(errorEnvIdents.entrySet().stream().map(status ->
                                RsStatusRapport.Status.builder()
                                        .melding(status.getKey())
                                        .detaljert(status.getValue().entrySet().stream()
                                                .map(detaljert -> RsStatusRapport.Detaljert.builder()
                                                        .miljo(detaljert.getKey())
                                                        .identer(newArrayList(detaljert.getValue()))
                                                        .build())
                                                .collect(Collectors.toList()))
                                        .build())
                                .collect(Collectors.toList()))
                        .build());
    }

    private static void checkNUpdateStatus(Map<String, Map<String, Set<String>>> errorEnvIdents, String ident, String environ, String status) {

        if (!errorEnvIdents.containsKey(SUCCESS) || !errorEnvIdents.get(SUCCESS).containsKey(environ) || !errorEnvIdents.get(SUCCESS).get(environ).contains(ident)) {
            if (errorEnvIdents.containsKey(status)) {
                if (errorEnvIdents.get(status).containsKey(environ)) {
                    errorEnvIdents.get(status).get(environ).add(ident);
                } else {
                    errorEnvIdents.get(status).put(environ, newHashSet(ident));
                }
            } else {
                Map<String, Set<String>> entry = new HashMap();
                entry.put(environ, newHashSet(ident));
                errorEnvIdents.put(status, entry);
            }
        }
    }
}