package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import com.github.saphyra.apphub.api.user.client.UserDataApiClient;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import com.github.saphyra.apphub.service.platform.main_gateway.service.AccessTokenQueryService;
import com.github.saphyra.util.CookieUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UserSettingApphubLocaleResolverTest {
    private static final String ACCESS_TOKEN_ID = "access-token-id";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String DEFAULT_LOCALE = "default-locale";
    private static final String LOCALE = "locale";

    @Mock
    private AccessTokenQueryService accessTokenQueryService;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private UserDataApiClient userDataApi;

    @InjectMocks
    private UserSettingLocaleResolver underTest;

    @Mock
    private HttpServletRequest request;

    @Test
    public void getLocale_cookieNotPresent() {
        given(cookieUtil.getCookie(request, Constants.ACCESS_TOKEN_COOKIE)).willReturn(Optional.empty());

        Optional<String> result = underTest.getLocale(request);

        assertThat(result).isEmpty();
    }

    @Test
    public void getLocale_accessTokenNotFound() {
        given(cookieUtil.getCookie(request, Constants.ACCESS_TOKEN_COOKIE)).willReturn(Optional.of(ACCESS_TOKEN_ID));
        given(accessTokenQueryService.getAccessToken(ACCESS_TOKEN_ID)).willReturn(Optional.empty());

        Optional<String> result = underTest.getLocale(request);

        assertThat(result).isEmpty();
    }

    @Test
    public void getLocale_languageNotFound() {
        given(cookieUtil.getCookie(request, Constants.ACCESS_TOKEN_COOKIE)).willReturn(Optional.of(ACCESS_TOKEN_ID));
        given(accessTokenQueryService.getAccessToken(ACCESS_TOKEN_ID)).willReturn(Optional.of(InternalAccessTokenResponse.builder().userId(USER_ID).build()));
        given(commonConfigProperties.getDefaultLocale()).willReturn(DEFAULT_LOCALE);
        given(userDataApi.getLanguage(USER_ID, DEFAULT_LOCALE)).willThrow(new RuntimeException());

        Optional<String> result = underTest.getLocale(request);

        assertThat(result).isEmpty();
    }

    @Test
    public void getLocale() {
        given(cookieUtil.getCookie(request, Constants.ACCESS_TOKEN_COOKIE)).willReturn(Optional.of(ACCESS_TOKEN_ID));
        given(accessTokenQueryService.getAccessToken(ACCESS_TOKEN_ID)).willReturn(Optional.of(InternalAccessTokenResponse.builder().userId(USER_ID).build()));
        given(commonConfigProperties.getDefaultLocale()).willReturn(DEFAULT_LOCALE);
        given(userDataApi.getLanguage(USER_ID, DEFAULT_LOCALE)).willReturn(LOCALE);

        Optional<String> result = underTest.getLocale(request);

        assertThat(result).contains(LOCALE);
    }
}