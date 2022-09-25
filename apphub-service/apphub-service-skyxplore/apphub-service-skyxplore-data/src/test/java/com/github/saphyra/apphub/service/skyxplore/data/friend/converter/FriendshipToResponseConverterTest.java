package com.github.saphyra.apphub.service.skyxplore.data.friend.converter;

import com.github.saphyra.apphub.api.skyxplore.response.FriendshipResponse;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class FriendshipToResponseConverterTest {
    private static final UUID FRIENDSHIP_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID_1 = UUID.randomUUID();
    private static final UUID FRIEND_ID_2 = UUID.randomUUID();
    private static final String FRIEND_NAME = "friend-name";

    @Mock
    private FriendNameQueryService friendNameQueryService;

    @InjectMocks
    private FriendshipToResponseConverter underTest;

    @Test
    public void convert() {
        Friendship friendship = Friendship.builder()
            .friendshipId(FRIENDSHIP_ID)
            .friend1(FRIEND_ID_1)
            .friend2(FRIEND_ID_2)
            .build();

        given(friendNameQueryService.getFriendName(friendship, FRIEND_ID_1)).willReturn(FRIEND_NAME);

        FriendshipResponse result = underTest.convert(friendship, FRIEND_ID_1);

        assertThat(result.getFriendshipId()).isEqualTo(FRIENDSHIP_ID);
        assertThat(result.getFriendId()).isEqualTo(FRIEND_ID_2);
        assertThat(result.getFriendName()).isEqualTo(FRIEND_NAME);
    }
}