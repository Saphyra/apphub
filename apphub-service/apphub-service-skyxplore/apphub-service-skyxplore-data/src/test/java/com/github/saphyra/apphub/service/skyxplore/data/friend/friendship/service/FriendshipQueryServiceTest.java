package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service;

import com.github.saphyra.apphub.api.skyxplore.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.service.skyxplore.data.friend.converter.FriendshipToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class FriendshipQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

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
    public void getFriends() {
        given(friendshipDao.getByFriendId(USER_ID)).willReturn(Arrays.asList(friendship));
        given(friendshipToResponseConverter.convert(friendship, USER_ID)).willReturn(friendshipResponse);

        List<FriendshipResponse> result = underTest.getFriends(USER_ID);

        assertThat(result).containsExactly(friendshipResponse);
    }
}