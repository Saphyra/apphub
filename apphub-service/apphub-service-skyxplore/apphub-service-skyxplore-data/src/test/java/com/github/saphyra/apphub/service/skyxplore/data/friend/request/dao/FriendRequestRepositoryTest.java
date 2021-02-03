package com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class FriendRequestRepositoryTest {
    private static final String FRIEND_REQUEST_ID_1 = "friend-request-id-1";
    private static final String FRIEND_REQUEST_ID_2 = "friend-request-id-2";
    private static final String FRIEND_REQUEST_ID_3 = "friend-request-id-3";
    private static final String ID_1 = "id-1";
    private static final String ID_2 = "id-2";
    private static final String ID_3 = "id-3";
    @Autowired
    private FriendRequestRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteBySenderIdOrFriendId() {
        FriendRequestEntity entity1 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_1)
            .friendId(ID_1)
            .senderId(ID_2)
            .build();
        FriendRequestEntity entity2 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_2)
            .friendId(ID_3)
            .senderId(ID_1)
            .build();
        FriendRequestEntity entity3 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_3)
            .friendId(ID_3)
            .senderId(ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2, entity3));

        underTest.deleteBySenderIdOrFriendId(ID_1);

        assertThat(underTest.findAll()).containsExactly(entity3);
    }

    @Test
    public void findBySenderIdAndFriendId() {
        FriendRequestEntity entity1 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_1)
            .friendId(ID_1)
            .senderId(ID_2)
            .build();
        FriendRequestEntity entity2 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_2)
            .friendId(ID_3)
            .senderId(ID_1)
            .build();
        FriendRequestEntity entity3 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_3)
            .friendId(ID_3)
            .senderId(ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2, entity3));

        Optional<FriendRequestEntity> result = underTest.findBySenderIdAndFriendId(ID_1, ID_2);

        assertThat(result).contains(entity1);
    }

    @Test
    public void getBySenderId() {
        FriendRequestEntity entity1 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_1)
            .friendId(ID_1)
            .senderId(ID_2)
            .build();
        FriendRequestEntity entity2 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_2)
            .friendId(ID_3)
            .senderId(ID_1)
            .build();
        FriendRequestEntity entity3 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_3)
            .friendId(ID_3)
            .senderId(ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2, entity3));

        List<FriendRequestEntity> result = underTest.getBySenderId(ID_1);

        assertThat(result).containsExactly(entity2);
    }

    @Test
    public void getByFriendId() {
        FriendRequestEntity entity1 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_1)
            .friendId(ID_1)
            .senderId(ID_2)
            .build();
        FriendRequestEntity entity2 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_2)
            .friendId(ID_3)
            .senderId(ID_1)
            .build();
        FriendRequestEntity entity3 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_3)
            .friendId(ID_3)
            .senderId(ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2, entity3));

        List<FriendRequestEntity> result = underTest.getByFriendId(ID_1);

        assertThat(result).containsExactly(entity1);
    }
}