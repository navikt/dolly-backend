package no.nav.dolly.aareg;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.dolly.fasit.FasitApiConsumer;
import no.nav.dolly.fasit.FasitResourceScope;
import no.nav.dolly.fasit.FasitResourceWithUnmappedProperties;

@RunWith(MockitoJUnitRunner.class)
public class AaregArbeidsforholdFasitConsumerTest {

    @Mock
    private FasitApiConsumer fasitApiConsumer;

    @InjectMocks
    private AaregArbeidsforholdFasitConsumer aaregArbeidsforholdFasitConsumer;

    @Before
    public void setup() {
        Map<String, String> map = new HashMap<>();
        map.put("url", "BaseUrl");

        when(fasitApiConsumer.fetchResources(anyString(), anyString()))
                .thenReturn(new FasitResourceWithUnmappedProperties[] {
                        FasitResourceWithUnmappedProperties.builder()
                                .scope(FasitResourceScope.builder()
                                        .environment("t0")
                                        .zone("fss")
                                        .build())
                                .properties(map)
                                .build()
                });
    }

    @Test
    public void fetchWsUrslAllEnvironments_OK() {

        Map<String, String> fasitConsumers = aaregArbeidsforholdFasitConsumer.fetchWsUrlsAllEnvironments();

        assertThat(fasitConsumers.get("t0"), is(equalTo("BaseUrl/aareg-core/BehandleArbeidsforholdService/v1")));
    }

    @Test
    public void fetchRestUrlsAllEnvironments_OK() {

        Map<String, String> fasitConsumers = aaregArbeidsforholdFasitConsumer.fetchRestUrlsAllEnvironments();

        assertThat(fasitConsumers.get("t0"), is(equalTo("BaseUrl/v1/arbeidstaker/arbeidsforhold")));
    }
}