package com.github.saphyra.apphub.service.user.authentication.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AccessTokenDaoTest {
    private static final OffsetDateTime EXPIRATION = OffsetDateTime.now();

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
}