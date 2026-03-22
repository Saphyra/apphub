package com.github.saphyra.apphub.service.community.friendship.dao.friend;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class FriendshipConverterTest {
    private static final UUID FRIENDSHIP_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID = UUID.randomUUID();
    private static final String FRIENDSHIP_ID_STRING = "friendship-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String FRIEND_ID_STRING = "friend-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private FriendshipConverter underTest;

    @Test
    public void convertDomain() {
        Friendship friendship = Friendship.builder()
            .friendshipId(FRIENDSHIP_ID)
            .userId(USER_ID)
            .friendId(FRIEND_ID)
            .build();

        given(uuidConverter.convertDomain(FRIENDSHIP_ID)).willReturn(FRIENDSHIP_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(FRIEND_ID)).willReturn(FRIEND_ID_STRING);

        FriendshipEntity result = underTest.convertDomain(friendship);

        assertThat(result.getFriendshipId()).isEqualTo(FRIENDSHIP_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getFriendId()).isEqualTo(FRIEND_ID_STRING);
    }

    @Test
    public void convertEntity() {
        FriendshipEntity entity = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_STRING)
            .userId(USER_ID_STRING)
            .friendId(FRIEND_ID_STRING)
            .build();

        given(uuidConverter.convertEntity(FRIENDSHIP_ID_STRING)).willReturn(FRIENDSHIP_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(FRIEND_ID_STRING)).willReturn(FRIEND_ID);

        Friendship result = underTest.convertEntity(entity);

        assertThat(result.getFriendshipId()).isEqualTo(FRIENDSHIP_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getFriendId()).isEqualTo(FRIEND_ID);
    }
}