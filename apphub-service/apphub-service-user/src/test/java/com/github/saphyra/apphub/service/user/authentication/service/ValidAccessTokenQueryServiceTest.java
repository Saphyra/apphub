package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.apphub.service.user.authentication.AuthenticationProperties;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.util.OffsetDateTimeProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ValidAccessTokenQueryServiceTest {
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final String ACCESS_TOKEN_ID_STRING = "access-token-id";
    private static final Integer EXPIRATION_MINUTES = 132;
    private static final OffsetDateTime CURRENT_DATE = OffsetDateTime.now();

    @Mock
    private AccessTokenDao accessTokenDao;

    @Mock
    private AuthenticationProperties authenticationProperties;

    @Mock
    private OffsetDateTimeProvider offsetDateTimeProvider;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ValidAccessTokenQueryService underTest;

    @Mock
    private AccessToken accessToken;

    @Before
    public void setUp() {
        given(uuidConverter.convertDomain(ACCESS_TOKEN_ID)).willReturn(ACCESS_TOKEN_ID_STRING);
        given(authenticationProperties.getAccessTokenExpirationMinutes()).willReturn(EXPIRATION_MINUTES);
        given(offsetDateTimeProvider.getCurrentDate()).willReturn(CURRENT_DATE);
    }

    @Test
    public void notFound() {
        given(accessTokenDao.findById(ACCESS_TOKEN_ID_STRING)).willReturn(Optional.empty());

        Optional<AccessToken> result = underTest.findByAccessTokenId(ACCESS_TOKEN_ID);

        assertThat(result).isEmpty();
    }

    @Test
    public void expired() {
        given(accessTokenDao.findById(ACCESS_TOKEN_ID_STRING)).willReturn(Optional.of(accessToken));
        given(accessToken.getLastAccess()).willReturn(CURRENT_DATE.minusMinutes(EXPIRATION_MINUTES).minusMinutes(1));

        Optional<AccessToken> result = underTest.findByAccessTokenId(ACCESS_TOKEN_ID);

        assertThat(result).isEmpty();
    }

    @Test
    public void persistent() {
        given(accessTokenDao.findById(ACCESS_TOKEN_ID_STRING)).willReturn(Optional.of(accessToken));
        given(accessToken.isPersistent()).willReturn(true);
        given(accessToken.getLastAccess()).willReturn(CURRENT_DATE.minusMinutes(EXPIRATION_MINUTES).minusMinutes(1));

        Optional<AccessToken> result = underTest.findByAccessTokenId(ACCESS_TOKEN_ID);

        assertThat(result).contains(accessToken);
    }

    @Test
    public void findByAccessTokenId() {
        given(accessTokenDao.findById(ACCESS_TOKEN_ID_STRING)).willReturn(Optional.of(accessToken));
        given(accessToken.getLastAccess()).willReturn(CURRENT_DATE.minusMinutes(EXPIRATION_MINUTES).plusMinutes(1));

        Optional<AccessToken> result = underTest.findByAccessTokenId(ACCESS_TOKEN_ID);

        assertThat(result).contains(accessToken);
    }
}