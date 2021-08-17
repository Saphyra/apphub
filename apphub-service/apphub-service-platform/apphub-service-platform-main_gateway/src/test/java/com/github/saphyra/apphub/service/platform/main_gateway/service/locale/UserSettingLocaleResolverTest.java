package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import com.github.saphyra.apphub.api.user.client.UserDataApiClient;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.service.platform.main_gateway.service.AccessTokenQueryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpCookie;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserSettingLocaleResolverTest {
    private static final String ACCESS_TOKEN_ID = "access-token-id";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String DEFAULT_LOCALE = "default-locale";
    private static final String LOCALE = "locale";

    @Mock
    private AccessTokenQueryService accessTokenQueryService;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @Mock
    private UserDataApiClient userDataApi;

    @InjectMocks
    private UserSettingLocaleResolver underTest;

    @Mock
    private HttpCookie cookie;

    @Mock
    private InternalAccessTokenResponse accessTokenResponse;

    private final MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();

    @Test
    public void accessTokenCookieNotPresent() {
        assertThat(underTest.getLocale(cookies)).isEmpty();
    }

    @Test
    public void accessTokenNotFound() {
        cookies.put(Constants.ACCESS_TOKEN_COOKIE, Arrays.asList(cookie));
        given(cookie.getValue()).willReturn(ACCESS_TOKEN_ID);
        given(accessTokenQueryService.getAccessToken(ACCESS_TOKEN_ID)).willReturn(Optional.empty());

        assertThat(underTest.getLocale(cookies)).isEmpty();
    }

    @Test
    public void languageQueryFailed() {
        cookies.put(Constants.ACCESS_TOKEN_COOKIE, Arrays.asList(cookie));
        given(cookie.getValue()).willReturn(ACCESS_TOKEN_ID);
        given(accessTokenQueryService.getAccessToken(ACCESS_TOKEN_ID)).willReturn(Optional.of(accessTokenResponse));
        given(accessTokenResponse.getUserId()).willReturn(USER_ID);
        given(commonConfigProperties.getDefaultLocale()).willReturn(DEFAULT_LOCALE);
        given(userDataApi.getLanguage(USER_ID, DEFAULT_LOCALE)).willThrow(new RuntimeException());

        assertThat(underTest.getLocale(cookies)).isEmpty();
    }

    @Test
    public void localeResolved() {
        cookies.put(Constants.ACCESS_TOKEN_COOKIE, Arrays.asList(cookie));
        given(cookie.getValue()).willReturn(ACCESS_TOKEN_ID);
        given(accessTokenQueryService.getAccessToken(ACCESS_TOKEN_ID)).willReturn(Optional.of(accessTokenResponse));
        given(accessTokenResponse.getUserId()).willReturn(USER_ID);
        given(commonConfigProperties.getDefaultLocale()).willReturn(DEFAULT_LOCALE);
        given(userDataApi.getLanguage(USER_ID, DEFAULT_LOCALE)).willReturn(LOCALE);

        assertThat(underTest.getLocale(cookies)).contains(LOCALE);
        assertThat(underTest.getLocale(cookies)).contains(LOCALE);

        verify(userDataApi, times(1)).getLanguage(any(), any());
    }

    @Test
    public void invalidate() {
        cookies.put(Constants.ACCESS_TOKEN_COOKIE, Arrays.asList(cookie));
        given(cookie.getValue()).willReturn(ACCESS_TOKEN_ID);
        given(accessTokenQueryService.getAccessToken(ACCESS_TOKEN_ID)).willReturn(Optional.of(accessTokenResponse));
        given(accessTokenResponse.getUserId()).willReturn(USER_ID);
        given(commonConfigProperties.getDefaultLocale()).willReturn(DEFAULT_LOCALE);
        given(userDataApi.getLanguage(USER_ID, DEFAULT_LOCALE)).willReturn(LOCALE);

        underTest.getLocale(cookies);

        underTest.invalidate(cookies);

        underTest.getLocale(cookies);
        verify(userDataApi, times(2)).getLanguage(any(), any());
    }
}