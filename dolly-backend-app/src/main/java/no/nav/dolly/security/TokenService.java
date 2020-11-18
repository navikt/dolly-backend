package no.nav.dolly.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    final OnBehalfOfGenerateAccessTokenService behalfOfGenerateAccessTokenService;

    public AccessToken getAccessToken(AccessScopes accessScopes) {
        return behalfOfGenerateAccessTokenService.generateToken(accessScopes);
    }
}
