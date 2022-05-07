package com.github.saphyra.apphub.service.community.friendship.dao.request;

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
public class FriendRequestRepositoryTest {
    private static final String FRIEND_REQUEST_ID_1 = "friend-request-id-1";
    private static final String FRIEND_REQUEST_ID_2 = "friend-request-id-2";
    private static final String FRIEND_REQUEST_ID_3 = "friend-request-id-3";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String USER_ID_3 = "user-id-3";

    @Autowired
    private FriendRequestRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Transactional
    @Test
    public void deleteBySenderIdAndReceiverId() {
        FriendRequestEntity entity1 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_1)
            .senderId(USER_ID_1)
            .receiverId(USER_ID_2)
            .build();
        FriendRequestEntity entity2 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_2)
            .senderId(USER_ID_2)
            .receiverId(USER_ID_1)
            .build();
        FriendRequestEntity entity3 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_3)
            .senderId(USER_ID_1)
            .receiverId(USER_ID_3)
            .build();
        underTest.saveAll(List.of(entity1, entity2, entity3));

        underTest.deleteBySenderIdAndReceiverId(USER_ID_1, USER_ID_2);

        assertThat(underTest.findAll()).containsExactly(entity3);
    }

    @Test
    public void getBySenderIdOrReceiverId() {
        FriendRequestEntity entity1 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_1)
            .senderId(USER_ID_1)
            .receiverId(USER_ID_2)
            .build();
        FriendRequestEntity entity2 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_2)
            .senderId(USER_ID_2)
            .receiverId(USER_ID_1)
            .build();
        FriendRequestEntity entity3 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_3)
            .senderId(USER_ID_2)
            .receiverId(USER_ID_3)
            .build();
        underTest.saveAll(List.of(entity1, entity2, entity3));

        List<FriendRequestEntity> result = underTest.getBySenderIdOrReceiverId(USER_ID_1);

        assertThat(result).containsExactlyInAnyOrder(entity1, entity2);
    }

    @Test
    public void getBySenderId() {
        FriendRequestEntity entity1 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_1)
            .senderId(USER_ID_1)
            .receiverId(USER_ID_2)
            .build();
        FriendRequestEntity entity2 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_2)
            .senderId(USER_ID_2)
            .receiverId(USER_ID_1)
            .build();
        FriendRequestEntity entity3 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_3)
            .senderId(USER_ID_1)
            .receiverId(USER_ID_3)
            .build();
        underTest.saveAll(List.of(entity1, entity2, entity3));

        List<FriendRequestEntity> result = underTest.getBySenderId(USER_ID_1);

        assertThat(result).containsExactlyInAnyOrder(entity1, entity3);
    }

    @Test
    public void getByReceiverId() {
        FriendRequestEntity entity1 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_1)
            .senderId(USER_ID_1)
            .receiverId(USER_ID_2)
            .build();
        FriendRequestEntity entity2 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_2)
            .senderId(USER_ID_2)
            .receiverId(USER_ID_1)
            .build();
        FriendRequestEntity entity3 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_3)
            .senderId(USER_ID_1)
            .receiverId(USER_ID_3)
            .build();
        underTest.saveAll(List.of(entity1, entity2, entity3));

        List<FriendRequestEntity> result = underTest.getByReceiverId(USER_ID_1);

        assertThat(result).containsExactly(entity2);
    }

    @Test
    public void findBySenderIdAndReceiverId() {
        FriendRequestEntity entity = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_1)
            .senderId(USER_ID_1)
            .receiverId(USER_ID_2)
            .build();
        underTest.save(entity);

        Optional<FriendRequestEntity> result = underTest.findBySenderIdAndReceiverId(USER_ID_1, USER_ID_2);

        assertThat(result).contains(entity);
    }

    @Test
    public void findBySenderIdAndReceiverId_reversed() {
        FriendRequestEntity entity = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_1)
            .senderId(USER_ID_1)
            .receiverId(USER_ID_2)
            .build();
        underTest.save(entity);

        Optional<FriendRequestEntity> result = underTest.findBySenderIdAndReceiverId(USER_ID_2, USER_ID_1);

        assertThat(result).contains(entity);
    }

    @Test
    @Transactional
    public void deleteByUserId() {
        FriendRequestEntity entity1 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_1)
            .senderId(USER_ID_1)
            .receiverId(USER_ID_2)
            .build();
        FriendRequestEntity entity2 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_2)
            .senderId(USER_ID_2)
            .receiverId(USER_ID_1)
            .build();
        FriendRequestEntity entity3 = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_3)
            .senderId(USER_ID_2)
            .receiverId(USER_ID_3)
            .build();
        underTest.saveAll(List.of(entity1, entity2, entity3));

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity3);
    }
}