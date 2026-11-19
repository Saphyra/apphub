package com.github.saphyra.apphub.api.platform.authorization.client;

import com.github.saphyra.apphub.api.platform.authorization.model.AuthorizationEndpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.DeleteExchange;

import java.util.UUID;

public interface AuthorizationClient {
    @DeleteExchange(AuthorizationEndpoints.INTERNAL_INVALIDATE_ALL_REFRESH_TOKENS)
    void invalidateAllRefreshTokens(@PathVariable("userId") UUID userId);

    @DeleteExchange(AuthorizationEndpoints.INTERNAL_INVALIDATE_ALL_ACCESS_TOKENS)
    void invalidateAllAccessTokens(@PathVariable("userId") UUID userId);
}

