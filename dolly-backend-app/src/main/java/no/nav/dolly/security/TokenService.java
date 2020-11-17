package no.nav.dolly.security;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.security.domain.AccessScopes;
import no.nav.dolly.security.domain.AccessToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    final OnBehalfOfGenerateAccessTokenService behalfOfGenerateAccessTokenService;

    public AccessToken getAccessToken(AccessScopes accessScopes) {
        return behalfOfGenerateAccessTokenService.generateToken(accessScopes);
    }
}
