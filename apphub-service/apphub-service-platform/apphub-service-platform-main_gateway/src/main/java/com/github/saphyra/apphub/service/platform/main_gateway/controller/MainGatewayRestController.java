package com.github.saphyra.apphub.service.platform.main_gateway.controller;

import com.github.saphyra.apphub.api.etc.user.model.UserEndpoints;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.config.common.GenericEndpoints;
import com.github.saphyra.apphub.service.platform.main_gateway.service.InvalidatedAccessTokenService;
import com.github.saphyra.apphub.service.platform.main_gateway.service.InvalidatedRefreshTokenService;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.TokenParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class MainGatewayRestController {
    private final InvalidatedAccessTokenService invalidatedAccessTokenService;
    private final InvalidatedRefreshTokenService invalidatedRefreshTokenService;
    private final TokenParser tokenParser;

    @PostMapping(UserEndpoints.EVENT_ACCESS_TOKEN_INVALIDATED)
    void accessTokenInvalidated(@RequestBody SendEventRequest<List<UUID>> sendEventRequest) {
        log.info("Invalidating accessTokens: {}", sendEventRequest.getPayload());
        sendEventRequest.getPayload()
            .forEach(invalidatedAccessTokenService::add);
    }

    @PostMapping(UserEndpoints.EVENT_REFRESH_TOKEN_INVALIDATED)
    void refreshTokenInvalidated(@RequestBody SendEventRequest<List<UUID>> sendEventRequest) {
        log.info("Invalidating refreshTokens: {}", sendEventRequest.getPayload());
        sendEventRequest.getPayload()
            .forEach(invalidatedRefreshTokenService::add);
    }

    @GetMapping("/api/ws/protocol")
    OneParamResponse<String> getWebSocketProtocol() {
        return new OneParamResponse<>("ws");
    }

    @GetMapping(GenericEndpoints.GET_OWN_USER_ID)
    Mono<ResponseEntity<OneParamResponse<UUID>>> getOwnUserId(@CookieValue(value = Constants.ACCESS_TOKEN_COOKIE, required = false) String accessTokenString) {
        return tokenParser.verifyAccessToken(accessTokenString)
            .map(AccessToken::getUserId)
            .map(OneParamResponse::new)
            .map(ResponseEntity::ok)
            .switchIfEmpty(Mono.just(new ResponseEntity<>(new OneParamResponse<>(null), HttpStatus.UNAUTHORIZED)));
    }

    @GetMapping("/invalidate-access-token/rest")
    Mono<ResponseEntity<Void>> invalidateAccessToken(@CookieValue(name = Constants.ACCESS_TOKEN_COOKIE, required = false) String accessTokenString) {
        return tokenParser.verifyAccessToken(accessTokenString)
            .map(accessToken -> {
                invalidatedAccessTokenService.add(accessToken.getAccessTokenId());

                return ResponseEntity.ok().build();
            });
    }
}
