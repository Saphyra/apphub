package com.github.saphyra.apphub.api.platform.authorization.server;

import com.github.saphyra.apphub.api.platform.authorization.model.AuthorizationEndpoints;
import com.github.saphyra.apphub.api.platform.authorization.model.LoginRequest;
import com.github.saphyra.apphub.api.platform.authorization.model.TokenResponse;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.github.saphyra.apphub.api.platform.authorization.model.AuthorizationEndpoints.AUTHORIZATION_LOGOUT;
import static com.github.saphyra.apphub.api.platform.authorization.model.AuthorizationEndpoints.AUTHORIZATION_REFRESH_TOKEN;

public interface AuthorizationController {
    @PostMapping(AuthorizationEndpoints.AUTHORIZATION_LOGIN)
    TokenResponse login(@RequestBody LoginRequest loginRequest);

    @PostMapping(AUTHORIZATION_LOGOUT)
    void logout(@CookieValue(Constants.REFRESH_TOKEN_COOKIE) String refreshToken);

    @PostMapping(AUTHORIZATION_REFRESH_TOKEN)
    TokenResponse refresh(@CookieValue(Constants.REFRESH_TOKEN_COOKIE) String refreshToken);
}
