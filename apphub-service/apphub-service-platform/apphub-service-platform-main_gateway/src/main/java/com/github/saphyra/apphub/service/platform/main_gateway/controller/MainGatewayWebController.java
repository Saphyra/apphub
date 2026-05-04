package com.github.saphyra.apphub.service.platform.main_gateway.controller;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.service.platform.main_gateway.service.InvalidatedAccessTokenService;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.TokenParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MainGatewayWebController {
    private final TokenParser tokenParser;
    private final InvalidatedAccessTokenService invalidatedAccessTokenService;

    @GetMapping("/invalidate-access-token/web")
    Mono<ResponseEntity<Void>> invalidateAccessToken(@CookieValue(name = Constants.ACCESS_TOKEN_COOKIE, required = false) String accessTokenString) {
        return tokenParser.verifyAccessToken(accessTokenString)
            .map(accessToken -> {
                invalidatedAccessTokenService.add(accessToken.getAccessTokenId());

                return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                    .location(URI.create("/web"))
                    .build();
            });
    }
}
