package com.github.saphyra.apphub.service.user.controller;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.user.model.request.LoginRequest;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.api.user.model.response.LastVisitedPageResponse;
import com.github.saphyra.apphub.api.user.model.response.LoginResponse;
import com.github.saphyra.apphub.api.user.server.UserAuthenticationController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.event.RefreshAccessTokenExpirationEvent;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.apphub.service.user.authentication.AuthenticationProperties;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.authentication.service.AccessTokenCleanupService;
import com.github.saphyra.apphub.service.user.authentication.service.AccessTokenUpdateService;
import com.github.saphyra.apphub.service.user.authentication.service.LoginService;
import com.github.saphyra.apphub.service.user.authentication.service.LogoutService;
import com.github.saphyra.apphub.service.user.authentication.service.ValidAccessTokenQueryService;
import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController implements UserAuthenticationController {
    private final AccessTokenCleanupService accessTokenCleanupService;
    private final AccessTokenUpdateService accessTokenUpdateService;
    private final AuthenticationProperties authenticationProperties;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final RoleDao roleDao;
    private final ValidAccessTokenQueryService validAccessTokenQueryService;
    private final AccessTokenDao accessTokenDao;

    @Override
    public void checkSession(AccessTokenHeader accessTokenHeader) {
        log.debug("Checking session for {}", accessTokenHeader);
    }

    @Override
    public void deleteExpiredAccessTokens() {
        log.info("Deleting expired accessTokens...");
        accessTokenCleanupService.deleteExpiredAccessTokens();
        log.info("Expired accessTokens deleted.");
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
            .orElseThrow(() -> new NotFoundException(userId + " is not logged in."));
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
            .map(this::convert)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private InternalAccessTokenResponse convert(AccessToken accessToken) {
        return InternalAccessTokenResponse.builder()
            .accessTokenId(accessToken.getAccessTokenId())
            .userId(accessToken.getUserId())
            .roles(getRoles(accessToken.getUserId()))
            .build();
    }

    private List<String> getRoles(UUID userId) {
        return roleDao.getByUserId(userId)
            .stream()
            .map(Role::getRole)
            .collect(Collectors.toList());
    }
}
