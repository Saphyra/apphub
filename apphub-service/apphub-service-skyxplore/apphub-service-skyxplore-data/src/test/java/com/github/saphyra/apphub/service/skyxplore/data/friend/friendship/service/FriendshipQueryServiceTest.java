package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service;

import com.github.saphyra.apphub.api.skyxplore.response.FriendshipResponse;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class FriendshipQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID FRIENDSHIP_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID = UUID.randomUUID();
    private static final String FRIEND_NAME = "friend-name";

    @Mock
    private FriendshipDao friendshipDao;

    @Mock
    private FriendNameQueryService friendNameQueryService;

    @Mock
    private FriendIdExtractor friendIdExtractor;

    @InjectMocks
    private FriendshipQueryService underTest;

    @Mock
    private Friendship friendship;

    @Test
    public void getFriends() {
        given(friendshipDao.getByFriendId(USER_ID)).willReturn(Arrays.asList(friendship));
        given(friendship.getFriendshipId()).willReturn(FRIENDSHIP_ID);
        given(friendIdExtractor.getFriendId(friendship, USER_ID)).willReturn(FRIEND_ID);
        given(friendNameQueryService.getFriendName(friendship, USER_ID)).willReturn(FRIEND_NAME);

        List<FriendshipResponse> result = underTest.getFriends(USER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFriendshipId()).isEqualTo(FRIENDSHIP_ID);
        assertThat(result.get(0).getFriendId()).isEqualTo(FRIEND_ID);
        assertThat(result.get(0).getFriendName()).isEqualTo(FRIEND_NAME);
    }
}