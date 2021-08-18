package com.github.saphyra.apphub.service.user.authentication;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.user.model.request.LoginRequest;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.api.user.model.response.LastVisitedPageResponse;
import com.github.saphyra.apphub.api.user.model.response.LoginResponse;
import com.github.saphyra.apphub.api.user.server.UserAuthenticationController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.event.RefreshAccessTokenExpirationEvent;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.authentication.service.AccessTokenCleanupService;
import com.github.saphyra.apphub.service.user.authentication.service.AccessTokenToResponseMapper;
import com.github.saphyra.apphub.service.user.authentication.service.AccessTokenUpdateService;
import com.github.saphyra.apphub.service.user.authentication.service.LoginService;
import com.github.saphyra.apphub.service.user.authentication.service.LogoutService;
import com.github.saphyra.apphub.service.user.authentication.service.ValidAccessTokenQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class AuthenticationController implements UserAuthenticationController {
    private final AccessTokenCleanupService accessTokenCleanupService;
    private final AccessTokenUpdateService accessTokenUpdateService;
    private final AuthenticationProperties authenticationProperties;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final AccessTokenToResponseMapper accessTokenToResponseMapper;
    private final ValidAccessTokenQueryService validAccessTokenQueryService;
    private final AccessTokenDao accessTokenDao;


    @Override
    public void checkSession(AccessTokenHeader accessTokenHeader) {
        log.debug("Checking session for {}", accessTokenHeader);
    }

    @Override
    public void deleteExpiredAccessTokens() {
        log.debug("Deleting expired accessTokens...");
        accessTokenCleanupService.deleteExpiredAccessTokens();
        log.debug("Expired accessTokens deleted.");
    }

    @Override
    public void refreshAccessTokenExpiration(SendEventRequest<RefreshAccessTokenExpirationEvent> request) {
        log.info("Updating expiration of accessToken with id {}", request.getPayload().getAccessTokenId());
        accessTokenUpdateService.updateLastAccess(request.getPayload().getAccessTokenId());
    }

    @Override
    public LastVisitedPageResponse getLastVisitedPage(UUID userId) {
        log.info("Querying last visited page of user {}", userId);
        return accessTokenDao.getByUserId(userId)
            .stream()
            .max(Comparator.comparing(AccessToken::getLastAccess))
            .map(accessToken -> LastVisitedPageResponse.builder()
                .lastAccess(accessToken.getLastAccess())
                .pageUrl(accessToken.getLastVisitedPage())
                .build())
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR, "User is not logged in."));
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
    public void logout(AccessTokenHeader accessToken) {
        logoutService.logout(accessToken.getAccessTokenId(), accessToken.getUserId());
    }

    @Override
    public ResponseEntity<InternalAccessTokenResponse> getAccessTokenById(UUID accessTokenId) {
        return validAccessTokenQueryService.findByAccessTokenId(accessTokenId)
            .map(accessTokenToResponseMapper::map)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
