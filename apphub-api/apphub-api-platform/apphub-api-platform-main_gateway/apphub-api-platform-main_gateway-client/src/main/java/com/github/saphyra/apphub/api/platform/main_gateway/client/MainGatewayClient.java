package com.github.saphyra.apphub.api.platform.main_gateway.client;

import com.github.saphyra.apphub.api.platform.main_gateway.model.MainGatewayEndpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;

import java.util.List;
import java.util.UUID;

public interface MainGatewayClient {
    @DeleteExchange(MainGatewayEndpoints.MAIN_GATEWAY_INTERNAL_INVALIDATE_ACCESS_TOKEN)
    void invalidateAccessToken(@PathVariable("accessTokenId") UUID accessTokenId);

    @DeleteExchange(MainGatewayEndpoints.MAIN_GATEWAY_INVALIDATE_REFRESH_TOKENS)
    void invalidateRefreshTokens(@RequestBody List<UUID> refreshTokenId);
}
