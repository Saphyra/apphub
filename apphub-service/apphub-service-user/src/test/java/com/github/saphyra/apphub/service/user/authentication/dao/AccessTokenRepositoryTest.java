package com.github.saphyra.apphub.service.user.authentication.dao;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class AccessTokenRepositoryTest {
    private static final String ACCESS_TOKEN_ID_1 = "access-token-id-1";
    private static final String ACCESS_TOKEN_ID_2 = "access-token-id-2";
    private static final String ACCESS_TOKEN_ID_3 = "access-token-id-3";
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";

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

    @Test
    public void updateLastAccess() {
        AccessTokenEntity entity1 = AccessTokenEntity.builder()
            .accessTokenId(ACCESS_TOKEN_ID_1)
            .lastAccess(CURRENT_DATE)
            .build();

        AccessTokenEntity entity2 = AccessTokenEntity.builder()
            .accessTokenId(ACCESS_TOKEN_ID_2)
            .lastAccess(CURRENT_DATE)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        LocalDateTime newLastAccess = CURRENT_DATE.plusHours(1);
        underTest.updateLastAccess(ACCESS_TOKEN_ID_1, newLastAccess);

        assertThat(underTest.findById(ACCESS_TOKEN_ID_1).get().getLastAccess()).isEqualTo(newLastAccess);
        assertThat(underTest.findById(ACCESS_TOKEN_ID_2).get().getLastAccess()).isEqualTo(CURRENT_DATE);
    }

    @Test
    public void deleteByAccessTokenIdAndUserId() {
        AccessTokenEntity entity1 = AccessTokenEntity.builder()
            .accessTokenId(ACCESS_TOKEN_ID_1)
            .userId(USER_ID_1)
            .build();

        AccessTokenEntity entity2 = AccessTokenEntity.builder()
            .accessTokenId(ACCESS_TOKEN_ID_2)
            .userId(USER_ID_1)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByAccessTokenIdAndUserId(ACCESS_TOKEN_ID_2, USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity1);
    }

    @Test
    @Transactional
    public void deleteByUserId() {
        AccessTokenEntity entity1 = AccessTokenEntity.builder()
            .accessTokenId(ACCESS_TOKEN_ID_1)
            .userId(USER_ID_1)
            .build();

        AccessTokenEntity entity2 = AccessTokenEntity.builder()
            .accessTokenId(ACCESS_TOKEN_ID_2)
            .userId(USER_ID_1)
            .build();

        AccessTokenEntity entity3 = AccessTokenEntity.builder()
            .accessTokenId(ACCESS_TOKEN_ID_3)
            .userId(USER_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2, entity3));

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity3);
    }
}