package com.github.saphyra.apphub.service.community.friendship.dao.friend;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class FriendshipRepositoryTest {
    private static final String FRIENDSHIP_ID_1 = "friendship-id-1";
    private static final String FRIENDSHIP_ID_2 = "friendship-id-2";
    private static final String FRIENDSHIP_ID_3 = "friendship-id-3";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String USER_ID_3 = "user-id-3";

    @Autowired
    private FriendshipRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Transactional
    @Test
    public void deleteByUserIdAndFriendId() {
        FriendshipEntity entity1 = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_1)
            .userId(USER_ID_1)
            .friendId(USER_ID_2)
            .build();
        FriendshipEntity entity2 = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_2)
            .userId(USER_ID_2)
            .friendId(USER_ID_1)
            .build();
        FriendshipEntity entity3 = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_3)
            .userId(USER_ID_1)
            .friendId(USER_ID_3)
            .build();
        underTest.saveAll(List.of(entity1, entity2, entity3));

        underTest.deleteByUserIdAndFriendId(USER_ID_1, USER_ID_2);

        assertThat(underTest.findAll()).containsExactly(entity3);
    }

    @Test
    public void getByUserIdOrFriendId() {
        FriendshipEntity entity1 = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_1)
            .userId(USER_ID_1)
            .friendId(USER_ID_2)
            .build();
        FriendshipEntity entity2 = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_2)
            .userId(USER_ID_2)
            .friendId(USER_ID_1)
            .build();
        FriendshipEntity entity3 = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_3)
            .userId(USER_ID_2)
            .friendId(USER_ID_3)
            .build();
        underTest.saveAll(List.of(entity1, entity2, entity3));

        List<FriendshipEntity> result = underTest.getByUserIdOrFriendId(USER_ID_1);

        assertThat(result).containsExactlyInAnyOrder(entity1, entity2);
    }

    @Test
    public void findByUserIdAndFriendId() {
        FriendshipEntity entity = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_1)
            .userId(USER_ID_1)
            .friendId(USER_ID_2)
            .build();
        underTest.save(entity);

        Optional<FriendshipEntity> result = underTest.findByUserIdAndFriendId(USER_ID_1, USER_ID_2);

        assertThat(result).contains(entity);
    }

    @Test
    public void findByUserIdAndFriendId_reversed() {
        FriendshipEntity entity = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_1)
            .userId(USER_ID_2)
            .friendId(USER_ID_1)
            .build();
        underTest.save(entity);

        Optional<FriendshipEntity> result = underTest.findByUserIdAndFriendId(USER_ID_1, USER_ID_2);

        assertThat(result).contains(entity);
    }

    @Test
    @Transactional
    public void deleteByUserId() {
        FriendshipEntity entity1 = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_1)
            .userId(USER_ID_1)
            .friendId(USER_ID_2)
            .build();
        FriendshipEntity entity2 = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_2)
            .userId(USER_ID_2)
            .friendId(USER_ID_1)
            .build();
        FriendshipEntity entity3 = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_3)
            .userId(USER_ID_2)
            .friendId(USER_ID_3)
            .build();
        underTest.saveAll(List.of(entity1, entity2, entity3));

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity3);
    }
}