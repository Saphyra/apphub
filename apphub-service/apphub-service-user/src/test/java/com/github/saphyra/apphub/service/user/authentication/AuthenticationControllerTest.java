package com.github.saphyra.apphub.service.user.authentication;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.user.model.request.LoginRequest;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.api.user.model.response.LastVisitedPageResponse;
import com.github.saphyra.apphub.api.user.model.response.LoginResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.event.RefreshAccessTokenExpirationEvent;
import com.github.saphyra.apphub.lib.exception.NotLoggedException;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.authentication.service.AccessTokenCleanupService;
import com.github.saphyra.apphub.service.user.authentication.service.AccessTokenToResponseMapper;
import com.github.saphyra.apphub.service.user.authentication.service.AccessTokenUpdateService;
import com.github.saphyra.apphub.service.user.authentication.service.LoginService;
import com.github.saphyra.apphub.service.user.authentication.service.LogoutService;
import com.github.saphyra.apphub.service.user.authentication.service.ValidAccessTokenQueryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationControllerTest {
    private static final Integer ACCESS_TOKEN_COOKIE_EXPIRATION_DAYS = 134;
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDateTime LAST_ACCESS = LocalDateTime.now();
    private static final String LAST_VISITED_PAGE = "last-visited-page";

    @Mock
    private AccessTokenCleanupService accessTokenCleanupService;

    @Mock
    private AuthenticationProperties authenticationProperties;

    @Mock
    private AccessTokenUpdateService accessTokenUpdateService;

    @Mock
    private LoginService loginService;

    @Mock
    private LogoutService logoutService;

    @Mock
    private AccessTokenToResponseMapper accessTokenToResponseMapper;

    @Mock
    private ValidAccessTokenQueryService validAccessTokenQueryService;

    @Mock
    private AccessTokenDao accessTokenDao;

    @InjectMocks
    private AuthenticationController underTest;

    @Mock
    private LoginRequest loginRequest;

    @Mock
    private AccessToken accessToken1;

    @Mock
    private AccessToken accessToken2;

    @Mock
    private InternalAccessTokenResponse accessTokenResponse;

    @Test
    public void deleteExpiredAccessTokens() {
        underTest.deleteExpiredAccessTokens();

        verify(accessTokenCleanupService).deleteExpiredAccessTokens();
    }

    @Test
    public void refreshAccessTokenExpiration() {
        underTest.refreshAccessTokenExpiration(SendEventRequest.<RefreshAccessTokenExpirationEvent>builder().payload(new RefreshAccessTokenExpirationEvent(ACCESS_TOKEN_ID)).build());

        verify(accessTokenUpdateService).updateLastAccess(ACCESS_TOKEN_ID);
    }


    @Test
    public void login_persistent() {
        given(authenticationProperties.getAccessTokenCookieExpirationDays()).willReturn(ACCESS_TOKEN_COOKIE_EXPIRATION_DAYS);
        given(loginService.login(loginRequest)).willReturn(accessToken1);
        given(accessToken1.getAccessTokenId()).willReturn(ACCESS_TOKEN_ID);
        given(accessToken1.isPersistent()).willReturn(true);

        LoginResponse result = underTest.login(loginRequest);

        assertThat(result.getAccessTokenId()).isEqualTo(ACCESS_TOKEN_ID);
        assertThat(result.getExpirationDays()).isEqualTo(ACCESS_TOKEN_COOKIE_EXPIRATION_DAYS);
    }

    @Test
    public void login_notPersistent() {
        given(loginService.login(loginRequest)).willReturn(accessToken1);
        given(accessToken1.getAccessTokenId()).willReturn(ACCESS_TOKEN_ID);
        given(accessToken1.isPersistent()).willReturn(false);

        LoginResponse result = underTest.login(loginRequest);

        assertThat(result.getAccessTokenId()).isEqualTo(ACCESS_TOKEN_ID);
        assertThat(result.getExpirationDays()).isNull();
    }

    @Test
    public void logout() {
        AccessTokenHeader accessTokenHeader = AccessTokenHeader.builder()
            .accessTokenId(ACCESS_TOKEN_ID)
            .userId(USER_ID)
            .build();

        underTest.logout(accessTokenHeader);

        verify(logoutService).logout(ACCESS_TOKEN_ID, USER_ID);
    }

    @Test
    public void getAccessTokenById() {
        given(validAccessTokenQueryService.findByAccessTokenId(ACCESS_TOKEN_ID)).willReturn(Optional.of(accessToken1));
        given(accessTokenToResponseMapper.map(accessToken1)).willReturn(accessTokenResponse);

        ResponseEntity<InternalAccessTokenResponse> result = underTest.getAccessTokenById(ACCESS_TOKEN_ID);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ////noinspection ConstantConditions
        assertThat(result.getBody()).isEqualTo(accessTokenResponse);
    }

    @Test
    public void getAccessTokenById_notFound() {
        given(validAccessTokenQueryService.findByAccessTokenId(ACCESS_TOKEN_ID)).willReturn(Optional.empty());

        ResponseEntity<InternalAccessTokenResponse> result = underTest.getAccessTokenById(ACCESS_TOKEN_ID);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test(expected = NotLoggedException.class)
    public void getLastVisitedPage_notFound() {
        given(accessTokenDao.getByUserId(USER_ID)).willReturn(Collections.emptyList());

        underTest.getLastVisitedPage(USER_ID);
    }

    @Test
    public void getLastVisitedPage() {
        given(accessTokenDao.getByUserId(USER_ID)).willReturn(Arrays.asList(accessToken1, accessToken2));
        given(accessToken1.getLastAccess()).willReturn(LAST_ACCESS);
        given(accessToken2.getLastAccess()).willReturn(LAST_ACCESS.plusSeconds(1));
        given(accessToken2.getLastVisitedPage()).willReturn(LAST_VISITED_PAGE);

        LastVisitedPageResponse result = underTest.getLastVisitedPage(USER_ID);

        assertThat(result.getLastAccess()).isEqualTo(LAST_ACCESS.plusSeconds(1));
        assertThat(result.getPageUrl()).isEqualTo(LAST_VISITED_PAGE);
    }
}