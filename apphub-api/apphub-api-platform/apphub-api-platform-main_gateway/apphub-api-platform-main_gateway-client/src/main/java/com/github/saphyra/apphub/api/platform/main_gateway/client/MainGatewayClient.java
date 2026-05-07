package com.github.saphyra.apphub.api.platform.main_gateway.client;

import com.github.saphyra.apphub.api.platform.main_gateway.model.MainGatewayEndpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "main-gateway", url = "${serviceUrls.mainGateway}")
public interface MainGatewayClient {
    @DeleteMapping(MainGatewayEndpoints.MAIN_GATEWAY_INTERNAL_INVALIDATE_ACCESS_TOKEN)
    void invalidateAccessToken(@PathVariable("accessTokenId") UUID accessTokenId);

    @DeleteMapping(MainGatewayEndpoints.MAIN_GATEWAY_INVALIDATE_REFRESH_TOKENS)
    void invalidateRefreshTokens(@RequestBody List<UUID> refreshTokenId);
}
