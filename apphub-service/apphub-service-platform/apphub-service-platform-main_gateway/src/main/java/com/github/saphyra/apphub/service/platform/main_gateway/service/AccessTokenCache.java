package com.github.saphyra.apphub.service.platform.main_gateway.service;

import com.github.saphyra.apphub.api.user.client.UserAuthenticationClient;
import com.github.saphyra.apphub.api.user.model.login.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_util.AbstractCache;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class AccessTokenCache extends AbstractCache<UUID, InternalAccessTokenResponse> {
    private final CommonConfigProperties commonConfigProperties;
    private final UserAuthenticationClient authenticationApi;

    public AccessTokenCache(CommonConfigProperties commonConfigProperties, UserAuthenticationClient authenticationApi) {
        super(CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.SECONDS).build());
        this.commonConfigProperties = commonConfigProperties;
        this.authenticationApi = authenticationApi;
    }

    @Override
    protected Optional<InternalAccessTokenResponse> load(UUID accessTokenId) {
        return Optional.of(authenticationApi.getAccessTokenById(accessTokenId, commonConfigProperties.getDefaultLocale()));
    }
}
