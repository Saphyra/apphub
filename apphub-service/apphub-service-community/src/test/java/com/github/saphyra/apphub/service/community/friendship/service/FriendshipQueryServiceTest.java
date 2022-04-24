package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.api.community.model.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.Friendship;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.FriendshipDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class FriendshipQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID = UUID.randomUUID();

    @Mock
    private FriendshipDao friendshipDao;

    @Mock
    private FriendshipToResponseConverter friendshipToResponseConverter;

    @InjectMocks
    private FriendshipQueryService underTest;

    @Mock
    private Friendship friendship;

    @Mock
    private FriendshipResponse friendshipResponse;

    @Test
    public void getFriendship() {
        given(friendshipDao.getByUserIdOrFriendId(USER_ID)).willReturn(List.of(friendship));
        given(friendship.getOtherUserId(USER_ID)).willReturn(FRIEND_ID);
        given(friendshipToResponseConverter.convert(friendship, FRIEND_ID)).willReturn(friendshipResponse);

        List<FriendshipResponse> result = underTest.getFriendships(USER_ID);

        assertThat(result).containsExactly(friendshipResponse);
    }
}