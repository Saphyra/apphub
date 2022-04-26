package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.api.community.model.response.friend_request.FriendRequestResponse;
import com.github.saphyra.apphub.service.community.friendship.dao.request.FriendRequest;
import com.github.saphyra.apphub.service.community.friendship.dao.request.FriendRequestDao;
import org.junit.Before;
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
public class FriendRequestQueryServiceTest {
    private static final UUID ID_TO_CONVERT = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private FriendRequestDao friendRequestDao;

    @Mock
    private FriendRequestToResponseConverter friendRequestToResponseConverter;

    @InjectMocks
    private FriendRequestQueryService underTest;

    @Mock
    private FriendRequest friendRequest;

    @Mock
    private FriendRequestResponse friendRequestResponse;

    @Before
    public void setUp() {
        given(friendRequestToResponseConverter.convert(friendRequest, ID_TO_CONVERT)).willReturn(friendRequestResponse);
    }

    @Test
    public void getSentFriendRequests() {
        given(friendRequestDao.getBySenderId(USER_ID)).willReturn(List.of(friendRequest));
        given(friendRequest.getReceiverId()).willReturn(ID_TO_CONVERT);

        List<FriendRequestResponse> result = underTest.getSentFriendRequests(USER_ID);

        assertThat(result).containsExactly(friendRequestResponse);
    }

    @Test
    public void getReceivedFriendRequests() {
        given(friendRequestDao.getByReceiverId(USER_ID)).willReturn(List.of(friendRequest));
        given(friendRequest.getSenderId()).willReturn(ID_TO_CONVERT);

        List<FriendRequestResponse> result = underTest.getReceivedFriendRequests(USER_ID);

        assertThat(result).containsExactly(friendRequestResponse);
    }
}