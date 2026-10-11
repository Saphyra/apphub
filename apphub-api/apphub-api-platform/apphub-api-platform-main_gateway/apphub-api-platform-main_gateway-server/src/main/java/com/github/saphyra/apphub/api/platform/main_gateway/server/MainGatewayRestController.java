package com.github.saphyra.apphub.api.platform.main_gateway.server;

import com.github.saphyra.apphub.api.platform.main_gateway.model.MainGatewayEndpoints;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface MainGatewayRestController {
    @DeleteMapping(MainGatewayEndpoints.MAIN_GATEWAY_INTERNAL_INVALIDATE_ACCESS_TOKEN)
    Mono<Void> invalidateAccessToken(@PathVariable("accessTokenId") UUID accessTokenId);

    @DeleteMapping(MainGatewayEndpoints.MAIN_GATEWAY_INVALIDATE_REFRESH_TOKENS)
    Mono<Void> invalidateRefreshTokens(@RequestBody List<UUID> refreshTokenIds);

    @DeleteMapping(MainGatewayEndpoints.MAIN_GATEWAY_UTIL_INVALIDATE_ACCESS_TOKEN)
    Mono<ResponseEntity<Object>> invalidateAccessToken(@CookieValue(name = Constants.ACCESS_TOKEN_COOKIE, required = false) String accessTokenString);

    @GetMapping(MainGatewayEndpoints.MAIN_GATEWAY_GET_WEBSOCKET_PROTOCOL)
    Mono<OneParamResponse<String>> getWebSocketProtocol();

    @GetMapping(MainGatewayEndpoints.MAIN_GATEWAY_UTIL_GET_OWN_USER_ID)
    Mono<ResponseEntity<OneParamResponse<UUID>>> getOwnUserId(@CookieValue(value = Constants.ACCESS_TOKEN_COOKIE, required = false) String accessToken);

    @GetMapping(MainGatewayEndpoints.MAIN_GATEWAY_CHECK_SESSION)
    Mono<ResponseEntity<Object>> checkSession(@CookieValue(value = Constants.ACCESS_TOKEN_COOKIE, required = false) String accessToken);
}
