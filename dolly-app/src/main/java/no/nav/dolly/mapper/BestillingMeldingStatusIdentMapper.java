package no.nav.dolly.mapper;

import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsMeldingStatusIdent;

public class BestillingMeldingStatusIdentMapper {

    private BestillingMeldingStatusIdentMapper() {

    }

    protected static Consumer<String> resolveStatus(Map<String, Map<String, List<String>>> msgStatusIdents, BestillingProgress progress) {

        return message -> {
            String[] melding = message.split("\\&");
            if (melding.length > 1) {
                String[] status = melding[1].split(",");

                if (msgStatusIdents.containsKey(melding[0])) {
                    appendStatusIdent(msgStatusIdents.get(melding[0]), progress, status[0]);
                } else {
                    Map<String, List<String>> statusIdent = new HashMap();
                    statusIdent.put(status[0], newArrayList(progress.getIdent()));
                    msgStatusIdents.put(melding[0], statusIdent);
                }
            }
        };
    }

    protected static void appendStatusIdent(Map<String, List<String>> stringListMap, BestillingProgress progress, String status) {

        if (stringListMap.containsKey(status)) {
            stringListMap.get(status).add(progress.getIdent());
        } else {
            stringListMap.put(status, newArrayList(progress.getIdent()));
        }
    }

    protected static List<RsMeldingStatusIdent> prepareResult(Map<String, Map<String, List<String>>> msgStatusIdents) {

        List<RsMeldingStatusIdent> result = new ArrayList();
        msgStatusIdents.keySet().forEach(melding ->
                result.add(RsMeldingStatusIdent.builder()
                        .melding(melding)
                        .statusIdent(msgStatusIdents.get(melding))
                        .build()));
        return result;
    }
}
