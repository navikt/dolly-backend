package no.nav.dolly.security;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.security.oauth2.domain.AccessScopes;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.domain.AccessTokenService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    final AccessTokenService behalfOfGenerateAccessTokenService;

    public AccessToken getAccessToken(AccessScopes accessScopes) {
        return behalfOfGenerateAccessTokenService.generateToken(accessScopes);
    }
}
