package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AccessTokenUpdateServiceTest {
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();

    @Mock
    private AccessTokenDao accessTokenDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private AccessTokenUpdateService underTest;

    @Test
    public void updateLastAccess() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_DATE);

        underTest.updateLastAccess(ACCESS_TOKEN_ID);

        verify(accessTokenDao).updateLastAccess(ACCESS_TOKEN_ID, CURRENT_DATE);
    }
}