package com.github.saphyra.apphub.service.skyxplore.data.friend;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.response.FriendshipResponse;
import com.github.saphyra.apphub.api.skyxplore.response.IncomingFriendRequestResponse;
import com.github.saphyra.apphub.api.skyxplore.response.SentFriendRequestResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service.FriendshipDeletionService;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service.FriendshipQueryService;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.service.FriendRequestAcceptService;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.service.FriendRequestCancelService;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.service.FriendRequestCreationService;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.service.FriendRequestQueryService;
import org.junit.Before;
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
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FriendDataControllerImplTest {
    private static final String QUERY_STRING = "query-string";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID = UUID.randomUUID();
    private static final UUID FRIEND_REQUEST_ID = UUID.randomUUID();
    private static final UUID FRIENDSHIP_ID = UUID.randomUUID();

    @Mock
    private FriendCandidateQueryService friendCandidateQueryService;

    @Mock
    private FriendRequestCreationService friendRequestCreationService;

    @Mock
    private FriendRequestQueryService friendRequestQueryService;

    @Mock
    private FriendRequestCancelService friendRequestCancelService;

    @Mock
    private FriendRequestAcceptService friendRequestAcceptService;

    @Mock
    private FriendshipQueryService friendshipQueryService;

    @Mock
    private FriendshipDeletionService friendshipDeletionService;

    @InjectMocks
    private FriendDataControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private SkyXploreCharacterModel model;

    @Mock
    private SentFriendRequestResponse sentFriendRequestResponse;

    @Mock
    private IncomingFriendRequestResponse incomingFriendRequestResponse;

    @Mock
    private FriendshipResponse friendshipResponse;

    @Before
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void getFriendCandidates() {
        given(friendCandidateQueryService.getFriendCandidates(USER_ID, QUERY_STRING)).willReturn(Arrays.asList(model));

        List<SkyXploreCharacterModel> result = underTest.getFriendCandidates(new OneParamRequest<>(QUERY_STRING), accessTokenHeader);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void createFriendRequest() {
        underTest.createFriendRequest(new OneParamRequest<>(FRIEND_ID), accessTokenHeader);

        verify(friendRequestCreationService).createFriendRequest(USER_ID, FRIEND_ID);
    }

    @Test
    public void getSentFriendRequests() {
        given(friendRequestQueryService.getSentFriendRequests(USER_ID)).willReturn(Arrays.asList(sentFriendRequestResponse));

        List<SentFriendRequestResponse> result = underTest.getSentFriendRequests(accessTokenHeader);

        assertThat(result).containsExactly(sentFriendRequestResponse);
    }

    @Test
    public void getIncomingFriendRequests() {
        given(friendRequestQueryService.getIncomingFriendRequests(USER_ID)).willReturn(Arrays.asList(incomingFriendRequestResponse));

        List<IncomingFriendRequestResponse> result = underTest.getIncomingFriendRequests(accessTokenHeader);

        assertThat(result).containsExactly(incomingFriendRequestResponse);
    }

    @Test
    public void cancelFriendRequest() {
        underTest.cancelFriendRequest(FRIEND_REQUEST_ID, accessTokenHeader);

        verify(friendRequestCancelService).cancelFriendRequest(USER_ID, FRIEND_REQUEST_ID);
    }

    @Test
    public void acceptFriendRequest() {
        underTest.acceptFriendRequest(FRIEND_REQUEST_ID, accessTokenHeader);

        verify(friendRequestAcceptService).accept(USER_ID, FRIEND_REQUEST_ID);
    }

    @Test
    public void getFriends() {
        given(friendshipQueryService.getFriends(USER_ID)).willReturn(Arrays.asList(friendshipResponse));

        List<FriendshipResponse> result = underTest.getFriends(accessTokenHeader);

        assertThat(result).containsExactly(friendshipResponse);
    }

    @Test
    public void removeFriend() {
        underTest.removeFriend(FRIENDSHIP_ID, accessTokenHeader);

        verify(friendshipDeletionService).removeFriendship(FRIENDSHIP_ID, USER_ID);
    }
}