package no.nav.dolly.security;

import lombok.Data;

@Data
public abstract class ClientCredential {
    final String clientId;
    final String clientSecret;

    @Override
    public final String toString() {
        return "ClientCredential{" +
                "clientId=[HIDDEN]" +
                ", clientSecret=[HIDDEN]" +
                '}';
    }
}
