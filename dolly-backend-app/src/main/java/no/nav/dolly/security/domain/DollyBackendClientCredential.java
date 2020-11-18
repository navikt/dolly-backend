package no.nav.dolly.security.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DollyBackendClientCredential extends ClientCredential {
    public DollyBackendClientCredential(
            @Value("${CLIENT_ID}") String clientId,
            @Value("${CLIENT_SECRET}") String clientSecret
    ) {
        super(clientId, clientSecret);
    }
}
