package com.github.saphyra.apphub.service.platform.main_gateway.service;

import com.github.saphyra.apphub.service.platform.main_gateway.config.AuthorizationProperties;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
//TODO unit test
public class InvalidatedAccessTokenService {
    private final Cache<String, Boolean> invalidatedAccessTokens;

    InvalidatedAccessTokenService(AuthorizationProperties authorizationProperties) {
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
