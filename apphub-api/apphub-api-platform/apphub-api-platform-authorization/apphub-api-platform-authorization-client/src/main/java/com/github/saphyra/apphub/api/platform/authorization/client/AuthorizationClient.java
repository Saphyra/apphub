package com.github.saphyra.apphub.api.platform.authorization.client;

import com.github.saphyra.apphub.api.platform.authorization.model.AuthorizationEndpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "authorization", url = "${serviceUrls.authorization}")
public interface AuthorizationClient {
    @DeleteMapping(AuthorizationEndpoints.INTERNAL_INVALIDATE_ALL_REFRESH_TOKENS)
    void invalidateAllRefreshTokens(@PathVariable("userId") UUID userId);

    @DeleteMapping(AuthorizationEndpoints.INTERNAL_INVALIDATE_ALL_ACCESS_TOKENS)
    void invalidateAllAccessTokens(@PathVariable("userId") UUID userId);
}

