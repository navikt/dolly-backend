package no.nav.dolly.mapper;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient.FALSK_IDENTITET;
import static no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient.KONTAKTINFORMASJON_DOEDSBO;
import static no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient.UTENLANDS_IDENTIFIKASJONSNUMMER;
import static no.nav.dolly.domain.resultset.SystemTyper.PDL_DODSBO;
import static no.nav.dolly.domain.resultset.SystemTyper.PDL_FALSKID;
import static no.nav.dolly.domain.resultset.SystemTyper.PDL_FORVALTER;
import static no.nav.dolly.domain.resultset.SystemTyper.PDL_UTENLANDSID;
import static no.nav.dolly.mapper.BestillingMeldingStatusIdentMapper.resolveStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import no.nav.dolly.domain.resultset.SystemTyper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingPensjonStatusMapper {

    public static List<RsStatusRapport> buildPensjonStatusMap(List<BestillingProgress> progressList) {

        //  melding     status      ident
        Map<String, Map<String, List<String>>> msgStatusIdents = new HashMap();

        progressList.forEach(progress -> {
            if (nonNull(progress.getPensjonforvalterStatus())) {
                newArrayList(progress.getPensjonforvalterStatus().split("\\$")).forEach(
                        resolveStatus(msgStatusIdents, progress)
                );
            }
        });

        List<RsStatusRapport> statusRapporter = new ArrayList();
        statusRapporter.addAll(extractStatus(msgStatusIdents, KONTAKTINFORMASJON_DOEDSBO, PDL_DODSBO));
        statusRapporter.addAll(extractStatus(msgStatusIdents, UTENLANDS_IDENTIFIKASJONSNUMMER, PDL_UTENLANDSID));
        statusRapporter.addAll(extractStatus(msgStatusIdents, FALSK_IDENTITET, PDL_FALSKID));
        statusRapporter.addAll(extractStatus(msgStatusIdents, PdlForvalterClient.PDL_FORVALTER, PDL_FORVALTER));

        return statusRapporter;
    }

    private static List<RsStatusRapport> extractStatus(Map<String, Map<String, List<String>>> msgStatusIdents, String clientid, SystemTyper type) {
        return msgStatusIdents.entrySet().stream().filter(entry -> clientid.equals(entry.getKey()))
                .map(entry -> RsStatusRapport.builder().id(type).navn(type.getBeskrivelse())
                        .statuser(entry.getValue().entrySet().stream()
                                .map(entry1 -> RsStatusRapport.Status.builder()
                                        .melding(entry1.getKey().replaceAll(";",","))
                                        .detaljert(singletonList(RsStatusRapport.Detaljert.builder()
                                                .identer(entry1.getValue())
                                                .build()))
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

    }
}
