package com.github.saphyra.apphub.service.user.authentication.dao;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.OffsetDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class AccessTokenRepositoryTest {
    private static final String ACCESS_TOKEN_ID_1 = "access-token-id-1";
    private static final String ACCESS_TOKEN_ID_2 = "access-token-id-2";
    private static final String ACCESS_TOKEN_ID_3 = "access-token-id-3";
    private static final OffsetDateTime CURRENT_DATE = OffsetDateTime.now();

    @Autowired
    private AccessTokenRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    public void deletePersistentBefore() {
        AccessTokenEntity entity1 = AccessTokenEntity.builder()
            .accessTokenId(ACCESS_TOKEN_ID_1)
            .lastAccess(CURRENT_DATE.minusSeconds(1))
            .persistent(false)
            .build();

        AccessTokenEntity entity2 = AccessTokenEntity.builder()
            .accessTokenId(ACCESS_TOKEN_ID_2)
            .lastAccess(CURRENT_DATE.minusSeconds(1))
            .persistent(true)
            .build();

        AccessTokenEntity entity3 = AccessTokenEntity.builder()
            .accessTokenId(ACCESS_TOKEN_ID_3)
            .lastAccess(CURRENT_DATE)
            .persistent(true)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2, entity3));

        underTest.deleteByPersistentAndLastAccessBefore(true, CURRENT_DATE);

        assertThat(underTest.findAll()).containsExactlyInAnyOrder(entity1, entity3);
    }

    @Test
    public void deleteNonPersistentBefore() {
        AccessTokenEntity entity1 = AccessTokenEntity.builder()
            .accessTokenId(ACCESS_TOKEN_ID_1)
            .lastAccess(CURRENT_DATE.minusSeconds(1))
            .persistent(false)
            .build();

        AccessTokenEntity entity2 = AccessTokenEntity.builder()
            .accessTokenId(ACCESS_TOKEN_ID_2)
            .lastAccess(CURRENT_DATE.minusSeconds(1))
            .persistent(true)
            .build();

        AccessTokenEntity entity3 = AccessTokenEntity.builder()
            .accessTokenId(ACCESS_TOKEN_ID_3)
            .lastAccess(CURRENT_DATE)
            .persistent(false)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2, entity3));

        underTest.deleteByPersistentAndLastAccessBefore(false, CURRENT_DATE);

        assertThat(underTest.findAll()).containsExactlyInAnyOrder(entity2, entity3);
    }
}