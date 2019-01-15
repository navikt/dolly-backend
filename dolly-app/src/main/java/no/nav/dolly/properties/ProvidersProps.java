package no.nav.dolly.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "providers")
public class ProvidersProps {

    private Tpsf tpsf = new Tpsf();
    private SigrunStub sigrunStub = new SigrunStub();
    private KrrStub krrStub = new KrrStub();
    private Kodeverk kodeverk = new Kodeverk();
    private Norg2 norg2 = new Norg2();

    @Getter
    @Setter
    public static class Tpsf {
        private String url;
    }

    @Getter
    @Setter
    public static class SigrunStub {

        private String url;
    }

    @Getter
    @Setter
    public static class KrrStub {

        private String url;
    }

    @Getter
    @Setter
    public static class Kodeverk {

        private String url;
    }

    @Getter
    @Setter
    public static class Norg2 {

        private String url;
    }
}
