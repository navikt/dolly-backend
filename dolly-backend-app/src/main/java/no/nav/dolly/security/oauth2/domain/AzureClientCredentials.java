package no.nav.dolly.security.oauth2.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureClientCredentials {
    private final String clientId;
    private final String clientSecret;

    /**
     * @param clientId TODO nullable fra client id når alle apper er overført til AzureAd
     */
    public AzureClientCredentials(
            @Value("${CLIENT_ID:#{null}}}") String clientId,
            @Value("${CLIENT_SECRET:#{null}}") String clientSecret
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public String toString() {
        return "AzureClientCredentials{" +
                "clientId='" + clientId + '\'' +
                ", clientSecret='[hidden]" + '\'' +
                '}';
    }
}
