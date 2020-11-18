package no.nav.dolly.security;

import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
public class AccessScopes {

    List<String> scopes;

    public AccessScopes(String scope) {
        this.scopes = Collections.singletonList("api://" + scope + "/.default");
    }
}
