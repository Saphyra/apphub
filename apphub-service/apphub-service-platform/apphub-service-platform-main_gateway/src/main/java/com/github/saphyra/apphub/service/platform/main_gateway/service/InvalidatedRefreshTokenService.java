package com.github.saphyra.apphub.service.platform.main_gateway.service;

import com.github.saphyra.apphub.service.platform.main_gateway.config.AuthorizationProperties;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Storing invalidated / deleted RefreshTokens is needed because AccessToken verification does not check account existence.
 * If the user is deleted (or force-logged out), the invalidated refresh tokens are stored, so AccessToken verification detects that the accessToken should not be accepted.
 */
@Component
@Slf4j
public class InvalidatedRefreshTokenService {
    private final Cache<String, Boolean> invalidatedAccessTokens;

    InvalidatedRefreshTokenService(AuthorizationProperties authorizationProperties) {
        invalidatedAccessTokens = CacheBuilder.newBuilder()
            .expireAfterWrite(authorizationProperties.getAccessTokenExpiration())
            .build();
    }

    public boolean contains(UUID accessTokenId) {
        return invalidatedAccessTokens.getIfPresent(accessTokenId.toString()) != null;
    }

    public void add(UUID accessTokenId) {
        invalidatedAccessTokens.put(accessTokenId.toString(), true);
    }
}
