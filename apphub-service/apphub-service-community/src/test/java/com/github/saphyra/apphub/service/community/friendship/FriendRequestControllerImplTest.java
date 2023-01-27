package com.github.saphyra.apphub.service.community.friendship;

import com.github.saphyra.apphub.api.community.model.response.SearchResultItem;
import com.github.saphyra.apphub.api.community.model.response.friend_request.FriendRequestResponse;
import com.github.saphyra.apphub.api.community.model.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.community.friendship.service.AcceptFriendRequestService;
import com.github.saphyra.apphub.service.community.friendship.service.FriendCandidateSearchService;
import com.github.saphyra.apphub.service.community.friendship.service.FriendRequestCreationService;
import com.github.saphyra.apphub.service.community.friendship.service.FriendRequestDeletionService;
import com.github.saphyra.apphub.service.community.friendship.service.FriendRequestQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FriendRequestControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String QUERY = "query";
    private static final UUID FRIEND_ID = UUID.randomUUID();
    private static final UUID FRIEND_REQUEST_ID = UUID.randomUUID();

    @Mock
    private FriendCandidateSearchService friendCandidateSearchService;

    @Mock
    private FriendRequestQueryService friendRequestQueryService;

    @Mock
    private FriendRequestCreationService friendRequestCreationService;

    @Mock
    private FriendRequestDeletionService friendRequestDeletionService;

    @Mock
    private AcceptFriendRequestService acceptFriendRequestService;

    @InjectMocks
    private FriendRequestControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private SearchResultItem searchResultItem;

    @Mock
    private FriendRequestResponse friendRequestResponse;

    @Mock
    private FriendshipResponse friendshipResponse;

    @BeforeEach
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void search() {
        given(friendCandidateSearchService.search(USER_ID, QUERY)).willReturn(List.of(searchResultItem));

        List<SearchResultItem> result = underTest.search(new OneParamRequest<>(QUERY), accessTokenHeader);

        assertThat(result).containsExactly(searchResultItem);
    }

    @Test
    public void getSentFriendRequests() {
        given(friendRequestQueryService.getSentFriendRequests(USER_ID)).willReturn(List.of(friendRequestResponse));

        List<FriendRequestResponse> result = underTest.getSentFriendRequests(accessTokenHeader);

        assertThat(result).containsExactly(friendRequestResponse);
    }

    @Test
    public void getReceivedFriendRequests() {
        given(friendRequestQueryService.getReceivedFriendRequests(USER_ID)).willReturn(List.of(friendRequestResponse));

        List<FriendRequestResponse> result = underTest.getReceivedFriendRequests(accessTokenHeader);

        assertThat(result).containsExactly(friendRequestResponse);
    }

    @Test
    public void create() {
        given(friendRequestCreationService.create(USER_ID, FRIEND_ID)).willReturn(friendRequestResponse);

        FriendRequestResponse result = underTest.create(new OneParamRequest<>(FRIEND_ID), accessTokenHeader);

        assertThat(result).isEqualTo(friendRequestResponse);
    }

    @Test
    public void delete() {
        underTest.delete(FRIEND_REQUEST_ID, accessTokenHeader);

        verify(friendRequestDeletionService).delete(USER_ID, FRIEND_REQUEST_ID);
    }

    @Test
    public void accept() {
        given(acceptFriendRequestService.accept(USER_ID, FRIEND_REQUEST_ID)).willReturn(friendshipResponse);

        FriendshipResponse result = underTest.accept(FRIEND_REQUEST_ID, accessTokenHeader);

        assertThat(result).isEqualTo(friendshipResponse);
    }
}