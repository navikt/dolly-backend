package no.nav.dolly.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class AuthenticationTokenResolver {

    private final OAuth2AuthorizedClientService clientService;

    public OAuth2AccessToken getToken() {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = oauth2AuthenticationToken();
        String clientRegistrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                clientRegistrationId,
                oAuth2AuthenticationToken.getName()
        );
        return client.getAccessToken();
    }

    private OAuth2AuthenticationToken oauth2AuthenticationToken() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(o -> o instanceof OAuth2AuthenticationToken)
                .map(OAuth2AuthenticationToken.class::cast)
                .orElseThrow(() -> new RuntimeException("Finner ikke Authentication Token"));
    }
}
