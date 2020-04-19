package com.github.saphyra.apphub.service.user.authentication;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.user.authentication.model.request.LoginRequest;
import com.github.saphyra.apphub.api.user.authentication.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.api.user.authentication.model.response.LoginResponse;
import com.github.saphyra.apphub.api.user.authentication.server.UserAuthenticationController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.event.DeleteExpiredAccessTokensEvent;
import com.github.saphyra.apphub.lib.event.RefreshAccessTokenExpirationEvent;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.service.AccessTokenCleanupService;
import com.github.saphyra.apphub.service.user.authentication.service.AccessTokenUpdateService;
import com.github.saphyra.apphub.service.user.authentication.service.LoginService;
import com.github.saphyra.apphub.service.user.authentication.service.LogoutService;
import com.github.saphyra.apphub.service.user.authentication.service.ValidAccessTokenQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO int test
//TODO api test
//TODO fe test
public class AuthenticationController implements UserAuthenticationController {
    private final AccessTokenCleanupService accessTokenCleanupService;
    private final AccessTokenUpdateService accessTokenUpdateService;
    private final AuthenticationProperties authenticationProperties;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final ValidAccessTokenQueryService validAccessTokenQueryService;

    @Override
    public void deleteExpiredAccessTokens(SendEventRequest<DeleteExpiredAccessTokensEvent> request) {
        log.info("Deleting expired accessTokens...");
        accessTokenCleanupService.deleteExpiredAccessTokens();
        log.info("Expired accessTokens deleted.");
    }

    @Override
    public void refreshAccessTokenExpiration(SendEventRequest<RefreshAccessTokenExpirationEvent> request) {
        log.info("{}", request);
        log.info("Updating expiration of accessToken with id {}", request.getPayload().getAccessTokenId());
        accessTokenUpdateService.updateLastAccess(request.getPayload().getAccessTokenId());
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("LoginRequest arrived: {}", loginRequest);
        AccessToken accessToken = loginService.login(loginRequest);
        Integer expiration = accessToken.isPersistent() ? authenticationProperties.getAccessTokenCookieExpirationDays() : null;
        log.info("Setting up accessToken cookie with accessTokenId {}, expiration {}", accessToken.getAccessTokenId(), expiration);
        return LoginResponse.builder()
            .accessTokenId(accessToken.getAccessTokenId())
            .expirationDays(expiration)
            .build();
    }

    @Override
    //TODO unit test
    public void logout(AccessTokenHeader accessToken) {
        logoutService.logout(accessToken.getAccessTokenId(), accessToken.getUserId());
    }

    @Override
    public ResponseEntity<InternalAccessTokenResponse> getAccessTokenById(UUID accessTokenId) {
        return validAccessTokenQueryService.findByAccessTokenId(accessTokenId)
            .map(this::convert)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private InternalAccessTokenResponse convert(AccessToken accessToken) {
        return InternalAccessTokenResponse.builder()
            .accessTokenId(accessToken.getAccessTokenId())
            .userId(accessToken.getUserId())
            .build();
    }
}
