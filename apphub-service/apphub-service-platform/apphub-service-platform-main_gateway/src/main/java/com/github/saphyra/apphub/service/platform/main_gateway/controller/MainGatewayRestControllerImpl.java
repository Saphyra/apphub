package com.github.saphyra.apphub.service.platform.main_gateway.controller;

import com.github.saphyra.apphub.api.platform.main_gateway.server.MainGatewayRestController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.exception.RestException;
import com.github.saphyra.apphub.service.platform.main_gateway.service.InvalidatedAccessTokenService;
import com.github.saphyra.apphub.service.platform.main_gateway.service.InvalidatedRefreshTokenService;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.TokenParser;
import com.github.saphyra.apphub.service.platform.main_gateway.util.ErrorLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class MainGatewayRestControllerImpl implements MainGatewayRestController {
    private final InvalidatedAccessTokenService invalidatedAccessTokenService;
    private final InvalidatedRefreshTokenService invalidatedRefreshTokenService;
    private final TokenParser tokenParser;
    private final ErrorLogger errorLogger;

    @Override
    public Mono<Void> invalidateAccessToken(UUID accessTokenId) {
        return Mono.fromRunnable(() -> {
            log.info("Invalidating accessToken with id {}", accessTokenId);

            invalidatedAccessTokenService.add(accessTokenId);
        });
    }

    @Override
    public Mono<Void> invalidateRefreshTokens(List<UUID> refreshTokenIds) {
        return Mono.fromRunnable(() -> {
            log.info("Invalidating refreshTokens with ids {}", refreshTokenIds);

            refreshTokenIds.forEach(invalidatedRefreshTokenService::add);
        });
    }

    @Override
    public Mono<ResponseEntity<Object>> invalidateAccessToken(String accessTokenString) {
        return tokenParser.verifyAccessToken(accessTokenString)
            .filter(accessToken -> !invalidatedAccessTokenService.contains(accessToken.getAccessTokenId()))
            .filter(accessToken -> !invalidatedRefreshTokenService.contains(accessToken.getRefreshTokenId()))
            .map(accessToken -> {
                invalidatedAccessTokenService.add(accessToken.getAccessTokenId());

                return ResponseEntity.ok().build();
            })
            .onErrorResume(this::handleError);
    }

    @Override
    public Mono<ResponseEntity<Object>> checkSession(String accessTokenString) {
        return tokenParser.verifyAccessToken(accessTokenString)
            .filter(accessToken -> !invalidatedAccessTokenService.contains(accessToken.getAccessTokenId()))
            .filter(accessToken -> !invalidatedRefreshTokenService.contains(accessToken.getRefreshTokenId()))
            .map(_ -> ResponseEntity.ok().build())
            .onErrorResume(this::handleError)
            .switchIfEmpty(Mono.fromSupplier(() -> new ResponseEntity<>(HttpStatus.UNAUTHORIZED)));
    }

    @Override
    public Mono<OneParamResponse<String>> getWebSocketProtocol() {
        return Mono.just(new OneParamResponse<>("ws"));
    }

    @Override
    public Mono<ResponseEntity<OneParamResponse<UUID>>> getOwnUserId(String accessTokenString) {
        return tokenParser.verifyAccessToken(accessTokenString)
            .map(AccessToken::getUserId)
            .map(OneParamResponse::new)
            .map(ResponseEntity::ok)
            .switchIfEmpty(Mono.just(new ResponseEntity<>(new OneParamResponse<>(null), HttpStatus.UNAUTHORIZED)));
    }

    private Mono<ResponseEntity<Object>> handleError(Throwable throwable) {
        errorLogger.log(throwable);

        if (throwable instanceof RestException e) {
            return Mono.just(ResponseEntity.status(e.getResponseStatus()).body(e.getErrorMessage()));
        }

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
}
