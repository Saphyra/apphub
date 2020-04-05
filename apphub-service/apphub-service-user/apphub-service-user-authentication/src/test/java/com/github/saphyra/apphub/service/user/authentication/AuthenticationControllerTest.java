package com.github.saphyra.apphub.service.user.authentication;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.user.authentication.model.request.LoginRequest;
import com.github.saphyra.apphub.api.user.authentication.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.api.user.authentication.model.response.LoginResponse;
import com.github.saphyra.apphub.lib.event.DeleteExpiredAccessTokensEvent;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.service.AccessTokenCleanupService;
import com.github.saphyra.apphub.service.user.authentication.service.LoginService;
import com.github.saphyra.apphub.service.user.authentication.service.ValidAccessTokenQueryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
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

    @Mock
    private AccessTokenCleanupService accessTokenCleanupService;

    @Mock
    private AuthenticationProperties authenticationProperties;

    @Mock
    private LoginService loginService;

    @Mock
    private ValidAccessTokenQueryService validAccessTokenQueryService;

    @InjectMocks
    private AuthenticationController underTest;

    @Mock
    private HttpServletResponse response;

    @Mock
    private LoginRequest loginRequest;

    @Mock
    private AccessToken accessToken;

    @Mock
    private SendEventRequest<DeleteExpiredAccessTokensEvent> sendEventRequest;

    @Test
    public void deleteExpiredAccessTokens() {
        underTest.deleteExpiredAccessTokens(sendEventRequest);

        verify(accessTokenCleanupService).deleteExpiredAccessTokens();
    }

    @Test
    public void login_persistent() {
        given(authenticationProperties.getAccessTokenCookieExpirationDays()).willReturn(ACCESS_TOKEN_COOKIE_EXPIRATION_DAYS);
        given(loginService.login(loginRequest)).willReturn(accessToken);
        given(accessToken.getAccessTokenId()).willReturn(ACCESS_TOKEN_ID);
        given(accessToken.isPersistent()).willReturn(true);

        LoginResponse result = underTest.login(loginRequest, response);

        assertThat(result.getAccessTokenId()).isEqualTo(ACCESS_TOKEN_ID);
        assertThat(result.getExpirationDays()).isEqualTo(ACCESS_TOKEN_COOKIE_EXPIRATION_DAYS);
    }

    @Test
    public void login_notPersistent() {
        given(loginService.login(loginRequest)).willReturn(accessToken);
        given(accessToken.getAccessTokenId()).willReturn(ACCESS_TOKEN_ID);
        given(accessToken.isPersistent()).willReturn(false);

        LoginResponse result = underTest.login(loginRequest, response);

        assertThat(result.getAccessTokenId()).isEqualTo(ACCESS_TOKEN_ID);
        assertThat(result.getExpirationDays()).isNull();
    }

    @Test
    public void getAccessTokenById() {
        given(validAccessTokenQueryService.findByAccessTokenId(ACCESS_TOKEN_ID)).willReturn(Optional.of(accessToken));
        given(accessToken.getAccessTokenId()).willReturn(ACCESS_TOKEN_ID);
        given(accessToken.getUserId()).willReturn(USER_ID);

        ResponseEntity<InternalAccessTokenResponse> result = underTest.getAccessTokenById(ACCESS_TOKEN_ID);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        //noinspection ConstantConditions
        assertThat(result.getBody().getAccessTokenId()).isEqualTo(ACCESS_TOKEN_ID);
        assertThat(result.getBody().getUserId()).isEqualTo(USER_ID);
    }

    @Test
    public void getAccessTokenById_notFound() {
        given(validAccessTokenQueryService.findByAccessTokenId(ACCESS_TOKEN_ID)).willReturn(Optional.empty());

        ResponseEntity<InternalAccessTokenResponse> result = underTest.getAccessTokenById(ACCESS_TOKEN_ID);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}