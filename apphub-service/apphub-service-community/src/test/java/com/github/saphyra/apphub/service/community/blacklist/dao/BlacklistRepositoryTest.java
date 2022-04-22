package com.github.saphyra.apphub.service.community.blacklist.dao;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class BlacklistRepositoryTest {
    private static final String BLACKLIST_ID_1 = "blacklist-id-1";
    private static final String BLACKLIST_ID_2 = "blacklist-id-2";
    private static final String BLACKLIST_ID_3 = "blacklist-id-3";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String BLOCKED_USER_ID_1 = "blocked-user-id-1";
    private static final String BLOCKED_USER_ID_2 = "blocked-user-id-2";

    @Autowired
    private BlacklistRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    public void getByUserId() {
        BlacklistEntity entity1 = BlacklistEntity.builder()
            .blacklistId(BLACKLIST_ID_1)
            .userId(USER_ID_1)
            .blockedUserId(BLOCKED_USER_ID_1)
            .build();
        BlacklistEntity entity2 = BlacklistEntity.builder()
            .blacklistId(BLACKLIST_ID_2)
            .userId(USER_ID_2)
            .blockedUserId(BLOCKED_USER_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        List<BlacklistEntity> result = underTest.getByUserId(USER_ID_1);

        assertThat(result).containsExactly(entity1);
    }

    @Test
    public void findByUserIdOrBlockedUserId_found() {
        BlacklistEntity entity1 = BlacklistEntity.builder()
            .blacklistId(BLACKLIST_ID_1)
            .userId(USER_ID_1)
            .blockedUserId(BLOCKED_USER_ID_1)
            .build();
        underTest.save(entity1);

        Optional<BlacklistEntity> result = underTest.findByUserIdOrBlockedUserId(USER_ID_1, BLOCKED_USER_ID_1);

        assertThat(result).contains(entity1);
    }

    @Test
    public void findByUserIdOrBlockedUserId_foundReversed() {
        BlacklistEntity entity1 = BlacklistEntity.builder()
            .blacklistId(BLACKLIST_ID_1)
            .userId(USER_ID_1)
            .blockedUserId(BLOCKED_USER_ID_1)
            .build();
        underTest.save(entity1);

        Optional<BlacklistEntity> result = underTest.findByUserIdOrBlockedUserId(BLOCKED_USER_ID_1, USER_ID_1);

        assertThat(result).contains(entity1);
    }

    @Test
    public void findByUserIdOrBlockedUserId_notFound() {
        BlacklistEntity entity1 = BlacklistEntity.builder()
            .blacklistId(BLACKLIST_ID_1)
            .userId(USER_ID_1)
            .blockedUserId(BLOCKED_USER_ID_2)
            .build();
        underTest.save(entity1);

        Optional<BlacklistEntity> result = underTest.findByUserIdOrBlockedUserId(BLOCKED_USER_ID_1, USER_ID_1);

        assertThat(result).isEmpty();
    }

    @Test
    public void getByUserIdOrBlockedUserId() {
        BlacklistEntity entity1 = BlacklistEntity.builder()
            .blacklistId(BLACKLIST_ID_1)
            .userId(USER_ID_1)
            .blockedUserId(BLOCKED_USER_ID_1)
            .build();
        BlacklistEntity entity2 = BlacklistEntity.builder()
            .blacklistId(BLACKLIST_ID_2)
            .userId(USER_ID_2)
            .blockedUserId(USER_ID_1)
            .build();
        BlacklistEntity entity3 = BlacklistEntity.builder()
            .blacklistId(BLACKLIST_ID_3)
            .userId(USER_ID_2)
            .blockedUserId(BLOCKED_USER_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2, entity3));

        List<BlacklistEntity> result = underTest.getByUserIdOrBlockedUserId(USER_ID_1);

        assertThat(result).containsExactlyInAnyOrder(entity1, entity2);
    }

    @Test
    @Transactional
    public void deleteByUserId() {
        BlacklistEntity entity1 = BlacklistEntity.builder()
            .blacklistId(BLACKLIST_ID_1)
            .userId(USER_ID_1)
            .blockedUserId(BLOCKED_USER_ID_1)
            .build();
        BlacklistEntity entity2 = BlacklistEntity.builder()
            .blacklistId(BLACKLIST_ID_2)
            .userId(USER_ID_2)
            .blockedUserId(USER_ID_1)
            .build();
        BlacklistEntity entity3 = BlacklistEntity.builder()
            .blacklistId(BLACKLIST_ID_3)
            .userId(USER_ID_2)
            .blockedUserId(BLOCKED_USER_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2, entity3));

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity3);
    }
}