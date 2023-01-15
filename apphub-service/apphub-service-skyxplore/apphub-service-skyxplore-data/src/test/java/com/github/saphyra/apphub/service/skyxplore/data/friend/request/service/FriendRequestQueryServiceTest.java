package com.github.saphyra.apphub.service.skyxplore.data.friend.request.service;

import com.github.saphyra.apphub.api.skyxplore.response.IncomingFriendRequestResponse;
import com.github.saphyra.apphub.api.skyxplore.response.SentFriendRequestResponse;
import com.github.saphyra.apphub.service.skyxplore.data.friend.converter.FriendRequestToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequestDao;
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
public class FriendRequestQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private FriendRequestToResponseConverter friendRequestToResponseConverter;

    @Mock
    private FriendRequestDao friendRequestDao;

    @InjectMocks
    private FriendRequestQueryService underTest;

    @Mock
    private FriendRequest friendRequest;

    @Mock
    private SentFriendRequestResponse sentFriendRequestResponse;

    @Mock
    private IncomingFriendRequestResponse incomingFriendRequestResponse;

    @Test
    public void getSentFriendRequests() {
        given(friendRequestDao.getBySenderId(USER_ID)).willReturn(Arrays.asList(friendRequest));
        given(friendRequestToResponseConverter.toSentFriendRequest(friendRequest)).willReturn(sentFriendRequestResponse);

        List<SentFriendRequestResponse> result = underTest.getSentFriendRequests(USER_ID);

        assertThat(result).containsExactly(sentFriendRequestResponse);
    }

    @Test
    public void getIncomingFriendRequests() {
        given(friendRequestDao.getByFriendId(USER_ID)).willReturn(Arrays.asList(friendRequest));
        given(friendRequestToResponseConverter.toIncomingFriendRequest(friendRequest)).willReturn(incomingFriendRequestResponse);

        List<IncomingFriendRequestResponse> result = underTest.getIncomingFriendRequests(USER_ID);

        assertThat(result).containsExactly(incomingFriendRequestResponse);
    }
}