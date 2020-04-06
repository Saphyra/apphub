package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.util.OffsetDateTimeProvider;
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
public class AccessTokenUpdateServiceTest {
    private static final OffsetDateTime CURRENT_DATE = OffsetDateTime.now();
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();

    @Mock
    private AccessTokenDao accessTokenDao;

    @Mock
    private OffsetDateTimeProvider offsetDateTimeProvider;

    @InjectMocks
    private AccessTokenUpdateService underTest;

    @Test
    public void updateLastAccess() {
        given(offsetDateTimeProvider.getCurrentDate()).willReturn(CURRENT_DATE);

        underTest.updateLastAccess(ACCESS_TOKEN_ID);

        verify(accessTokenDao).updateLastAccess(ACCESS_TOKEN_ID, CURRENT_DATE);
    }
}