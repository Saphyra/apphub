package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service;

import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FriendshipCreationServiceTest {
    private static final UUID SENDER_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID = UUID.randomUUID();

    @Mock
    private FriendshipFactory friendshipFactory;

    @Mock
    private FriendshipDao friendshipDao;

    @InjectMocks
    private FriendshipCreationService underTest;

    @Mock
    private FriendRequest friendRequest;

    @Mock
    private Friendship friendship;

    @Test
    public void fromRequest() {
        given(friendRequest.getSenderId()).willReturn(SENDER_ID);
        given(friendRequest.getFriendId()).willReturn(FRIEND_ID);
        given(friendshipFactory.create(FRIEND_ID, SENDER_ID)).willReturn(friendship);

        underTest.fromRequest(friendRequest);

        verify(friendshipDao).save(friendship);
    }
}