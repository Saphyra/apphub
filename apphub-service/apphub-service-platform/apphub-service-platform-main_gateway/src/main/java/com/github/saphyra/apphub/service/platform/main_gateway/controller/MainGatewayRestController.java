package com.github.saphyra.apphub.service.platform.main_gateway.controller;

import com.github.saphyra.apphub.api.etc.user.model.UserEndpoints;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.config.common.GenericEndpoints;
import com.github.saphyra.apphub.service.platform.main_gateway.service.InvalidatedAccessTokenService;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.TokenParser;
import lombok.RequiredArgsConstructor;
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
//TODO unit test
class MainGatewayRestController {
    private final InvalidatedAccessTokenService invalidateAccessTokenService;
    private final TokenParser tokenParser;

    @PostMapping(UserEndpoints.EVENT_ACCESS_TOKEN_INVALIDATED)
    void accessTokenInvalidated(@RequestBody SendEventRequest<List<UUID>> sendEventRequest) {
        sendEventRequest.getPayload()
            .forEach(invalidateAccessTokenService::add);
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
}
