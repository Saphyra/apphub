package com.github.saphyra.apphub.api.platform.authorization.server;

import com.github.saphyra.apphub.api.platform.authorization.model.AuthorizationEndpoints;
import com.github.saphyra.apphub.api.platform.authorization.model.LoginRequest;
import com.github.saphyra.apphub.api.platform.authorization.model.TokenResponse;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

import static com.github.saphyra.apphub.api.platform.authorization.model.AuthorizationEndpoints.AUTHORIZATION_LOGOUT;
import static com.github.saphyra.apphub.api.platform.authorization.model.AuthorizationEndpoints.AUTHORIZATION_REFRESH_TOKEN;

public interface AuthorizationController {
    @PostMapping(AuthorizationEndpoints.AUTHORIZATION_LOGIN)
    TokenResponse login(@RequestBody LoginRequest loginRequest);

    @PostMapping(AUTHORIZATION_LOGOUT)
    void logout(
        @CookieValue(name = Constants.REFRESH_TOKEN_COOKIE, required = false) String refreshToken,
        @CookieValue(name = Constants.ACCESS_TOKEN_COOKIE, required = false) String accessToken
    );

    @PostMapping(AUTHORIZATION_REFRESH_TOKEN)
    ResponseEntity<TokenResponse> refresh(@CookieValue(name = Constants.REFRESH_TOKEN_COOKIE, required = false) String refreshToken);

    @DeleteMapping(AuthorizationEndpoints.INTERNAL_DEACTIVATE_ALL_SESSIONS)
    void deactivateAllSessions(@PathVariable("userId") UUID userId);

    @DeleteMapping(AuthorizationEndpoints.INTERNAL_INVALIDATE_ALL_ACCESS_TOKENS)
    void invalidateAllAccessTokens(@PathVariable("userId") UUID userId);
}
