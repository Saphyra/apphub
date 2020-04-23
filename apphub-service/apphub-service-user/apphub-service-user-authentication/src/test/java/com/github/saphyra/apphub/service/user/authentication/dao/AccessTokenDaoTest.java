package com.github.saphyra.apphub.service.user.authentication.dao;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AccessTokenDaoTest {
    private static final OffsetDateTime EXPIRATION = OffsetDateTime.now();
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final OffsetDateTime CURRENT_DATE = OffsetDateTime.now();
    private static final String ACCESS_TOKEN_ID_STRING = "access-token-id";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private AccessTokenConverter converter;

    @Mock
    private AccessTokenRepository repository;

    @InjectMocks
    private AccessTokenDao underTest;

    @Test
    public void deleteByPersistentAndLastAccessBefore() {
        underTest.deleteByPersistentAndLastAccessBefore(true, EXPIRATION);

        verify(repository).deleteByPersistentAndLastAccessBefore(true, EXPIRATION);
    }

    @Test
    public void updateLastAccess() {
        given(uuidConverter.convertDomain(ACCESS_TOKEN_ID)).willReturn(ACCESS_TOKEN_ID_STRING);

        underTest.updateLastAccess(ACCESS_TOKEN_ID, CURRENT_DATE);

        verify(repository).updateLastAccess(ACCESS_TOKEN_ID_STRING, CURRENT_DATE);
    }

    @Test
    public void deleteByAccessTokenIdAndUserId() {
        given(uuidConverter.convertDomain(ACCESS_TOKEN_ID)).willReturn(ACCESS_TOKEN_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByAccessTokenIdAndUserId(ACCESS_TOKEN_ID, USER_ID);

        verify(repository).deleteByAccessTokenIdAndUserId(ACCESS_TOKEN_ID_STRING, USER_ID_STRING);
    }
}