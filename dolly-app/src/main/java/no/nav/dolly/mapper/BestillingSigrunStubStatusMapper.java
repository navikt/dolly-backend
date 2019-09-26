package no.nav.dolly.mapper;

import static java.util.Objects.nonNull;
import static no.nav.dolly.util.ListUtil.listOf;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusIdent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingSigrunStubStatusMapper {

    public static List<RsStatusIdent> buildSigrunStubStatusMap(List<BestillingProgress> progressList) {

        Map<String, List<String>> statusMap = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getSigrunstubStatus())) {
                if (statusMap.containsKey(progress.getSigrunstubStatus())) {
                    statusMap.get(progress.getSigrunstubStatus()).add(progress.getIdent());
                } else {
                    statusMap.put(progress.getSigrunstubStatus(), listOf(progress.getIdent()));
                }
            }
        });

        List<RsStatusIdent> identStatus = new ArrayList<>();
        statusMap.forEach((key, value) ->
                identStatus.add(RsStatusIdent.builder()
                        .statusMelding(key)
                        .identer(value)
                        .build())
        );
        return identStatus;
    }

}
