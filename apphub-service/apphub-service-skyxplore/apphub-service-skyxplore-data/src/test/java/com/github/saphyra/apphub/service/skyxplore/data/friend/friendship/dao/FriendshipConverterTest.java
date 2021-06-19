package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class FriendshipConverterTest {
    private static final String FRIENDSHIP_ID_STRING = "friendship-id";
    private static final String FRIEND_ID_1_STRING = "friend-id-1";
    private static final String FRIEND_ID_2_STRING = "friend-id-2";
    private static final UUID FRIENDSHIP_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID_1 = UUID.randomUUID();
    private static final UUID FRIEND_ID_2 = UUID.randomUUID();

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private FriendshipConverter underTest;

    @Test
    public void processEntity() {
        FriendshipEntity entity = FriendshipEntity.builder()
            .friendshipId(FRIENDSHIP_ID_STRING)
            .friend1(FRIEND_ID_1_STRING)
            .friend2(FRIEND_ID_2_STRING)
            .build();

        given(uuidConverter.convertEntity(FRIENDSHIP_ID_STRING)).willReturn(FRIENDSHIP_ID);
        given(uuidConverter.convertEntity(FRIEND_ID_1_STRING)).willReturn(FRIEND_ID_1);
        given(uuidConverter.convertEntity(FRIEND_ID_2_STRING)).willReturn(FRIEND_ID_2);

        Friendship result = underTest.convertEntity(entity);

        assertThat(result.getFriendshipId()).isEqualTo(FRIENDSHIP_ID);
        assertThat(result.getFriend1()).isEqualTo(FRIEND_ID_1);
        assertThat(result.getFriend2()).isEqualTo(FRIEND_ID_2);
    }

    @Test
    public void convertDomain() {
        Friendship domain = Friendship.builder()
            .friendshipId(FRIENDSHIP_ID)
            .friend1(FRIEND_ID_1)
            .friend2(FRIEND_ID_2)
            .build();

        given(uuidConverter.convertDomain(FRIENDSHIP_ID)).willReturn(FRIENDSHIP_ID_STRING);
        given(uuidConverter.convertDomain(FRIEND_ID_1)).willReturn(FRIEND_ID_1_STRING);
        given(uuidConverter.convertDomain(FRIEND_ID_2)).willReturn(FRIEND_ID_2_STRING);

        FriendshipEntity result = underTest.convertDomain(domain);

        assertThat(result.getFriendshipId()).isEqualTo(FRIENDSHIP_ID_STRING);
        assertThat(result.getFriend1()).isEqualTo(FRIEND_ID_1_STRING);
        assertThat(result.getFriend2()).isEqualTo(FRIEND_ID_2_STRING);
    }
}