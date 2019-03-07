package no.nav.dolly.mapper;

import static org.assertj.core.util.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsIdentStatus;

@RunWith(MockitoJUnitRunner.class)
public class BestillingSigrunStubStatusMapperTest {

    private static final List<BestillingProgress> RUN_STATUS = newArrayList(
            BestillingProgress.builder().ident("IDENT_1")
                    .sigrunstubStatus("OK")
                    .build(),
            BestillingProgress.builder().ident("IDENT_2")
                    .sigrunstubStatus("FEIL")
                    .build(),
            BestillingProgress.builder().ident("IDENT_3")
                    .sigrunstubStatus("OK")
                    .build(),
            BestillingProgress.builder().ident("IDENT_4")
                    .sigrunstubStatus("FEIL")
                    .build(),
            BestillingProgress.builder().ident("IDENT_5")
                    .sigrunstubStatus("OK")
                    .build()
    );

    @Test
    public void krrStubStatusMap() {

        List<RsIdentStatus> identStatuses = BestillingSigrunStubStatusMapper.buildSigrunStubStatusMap(RUN_STATUS);

        assertThat(identStatuses.get(0).getStatusMelding(), is(equalTo("FEIL")));
        assertThat(identStatuses.get(0).getIdenter(), containsInAnyOrder("IDENT_2", "IDENT_4"));

        assertThat(identStatuses.get(1).getStatusMelding(), is(equalTo("OK")));
        assertThat(identStatuses.get(1).getIdenter(), containsInAnyOrder("IDENT_1", "IDENT_3", "IDENT_5"));
    }
}