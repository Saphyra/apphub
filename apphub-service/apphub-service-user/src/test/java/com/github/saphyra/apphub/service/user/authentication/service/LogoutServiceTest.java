package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LogoutServiceTest {
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private AccessTokenDao accessTokenDao;

    @InjectMocks
    private LogoutService underTest;

    @Test
    public void logout() {
        underTest.logout(ACCESS_TOKEN_ID, USER_ID);

        verify(accessTokenDao).deleteByAccessTokenIdAndUserId(ACCESS_TOKEN_ID, USER_ID);
    }
}