package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.user.authentication.AuthenticationProperties;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AccessTokenCleanupServiceTest {
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();
    private static final Integer COOKIE_EXPIRATION_DAYS = 134;
    private static final Integer EXPIRATION_MINUTES = 324;

    @Mock
    private AccessTokenDao accessTokenDao;

    @Mock
    private AuthenticationProperties authenticationProperties;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private AccessTokenCleanupService underTest;

    @Test
    public void deleteExpiredAccessTokens() {
        given(dateTimeUtil.getCurrentTime()).willReturn(CURRENT_DATE);
        given(authenticationProperties.getAccessTokenCookieExpirationDays()).willReturn(COOKIE_EXPIRATION_DAYS);
        given(authenticationProperties.getAccessTokenExpirationMinutes()).willReturn(EXPIRATION_MINUTES);

        underTest.deleteExpiredAccessTokens();

        verify(accessTokenDao).deleteByPersistentAndLastAccessBefore(true, CURRENT_DATE.minusDays(COOKIE_EXPIRATION_DAYS));
        verify(accessTokenDao).deleteByPersistentAndLastAccessBefore(false, CURRENT_DATE.minusMinutes(EXPIRATION_MINUTES));
    }
}