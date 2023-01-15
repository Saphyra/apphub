package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
public class FriendshipRepositoryTest {
    private static final String FRIENDSHIP_ID_1 = "friendship-id-1";
    private static final String FRIENDSHIP_ID_2 = "friendship-id-2";
    private static final String FRIENDSHIP_ID_3 = "friendship-id-3";
    private static final String FRIEND_ID_1 = "friend-id-1";
    private static final String FRIEND_ID_2 = "friend-id-2";
    private static final String FRIEND_ID_3 = "friend-id-3";

    @Autowired
    private FriendshipRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    public void getByFriendId() {
        FriendshipEntity entity1 = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_1)
            .friend1(FRIEND_ID_1)
            .friend2(FRIEND_ID_2)
            .build();
        FriendshipEntity entity2 = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_2)
            .friend1(FRIEND_ID_3)
            .friend2(FRIEND_ID_1)
            .build();
        FriendshipEntity entity3 = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_3)
            .friend1(FRIEND_ID_3)
            .friend2(FRIEND_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2, entity3));

        List<FriendshipEntity> result = underTest.getByFriendId(FRIEND_ID_1);

        assertThat(result).containsExactlyInAnyOrder(entity1, entity2);
    }

    @Test
    @Transactional
    public void deleteByFriendId() {
        FriendshipEntity entity1 = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_1)
            .friend1(FRIEND_ID_1)
            .friend2(FRIEND_ID_2)
            .build();
        FriendshipEntity entity2 = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_2)
            .friend1(FRIEND_ID_3)
            .friend2(FRIEND_ID_1)
            .build();
        FriendshipEntity entity3 = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_3)
            .friend1(FRIEND_ID_3)
            .friend2(FRIEND_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2, entity3));

        underTest.deleteByFriendId(FRIEND_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity3);
    }

    @Test
    public void findByFriendIds() {
        FriendshipEntity entity1 = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_1)
            .friend1(FRIEND_ID_1)
            .friend2(FRIEND_ID_2)
            .build();
        FriendshipEntity entity2 = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_2)
            .friend1(FRIEND_ID_3)
            .friend2(FRIEND_ID_1)
            .build();
        FriendshipEntity entity3 = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_3)
            .friend1(FRIEND_ID_3)
            .friend2(FRIEND_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2, entity3));

        Optional<FriendshipEntity> result = underTest.findByFriendIds(FRIEND_ID_2, FRIEND_ID_1);

        assertThat(result).contains(entity1);
    }
}