package no.nav.dolly.provider;

import lombok.NoArgsConstructor;
import lombok.Value;
import no.nav.dolly.security.domain.AccessToken;

@Value
@NoArgsConstructor(force = true)
public class TokenDTO {
    public TokenDTO(AccessToken accessToken) {
        this.token = accessToken.getTokenValue();
    }

    String token;
}
