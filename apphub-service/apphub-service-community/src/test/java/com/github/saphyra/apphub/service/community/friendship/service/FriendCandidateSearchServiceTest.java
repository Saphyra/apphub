package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.api.community.model.response.SearchResultItem;
import com.github.saphyra.apphub.api.user.model.response.AccountResponse;
import com.github.saphyra.apphub.service.community.blacklist.service.BlockedUsersQueryService;
import com.github.saphyra.apphub.service.community.common.AccountClientProxy;
import com.github.saphyra.apphub.service.community.common.AccountResponseToSearchResultItemConverter;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.Friendship;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.FriendshipDao;
import com.github.saphyra.apphub.service.community.friendship.dao.request.FriendRequest;
import com.github.saphyra.apphub.service.community.friendship.dao.request.FriendRequestDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class FriendCandidateSearchServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String QUERY = "query";
    private static final UUID BLOCKED_USER_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID = UUID.randomUUID();
    private static final UUID RECEIVER_ID = UUID.randomUUID();

    @Mock
    private BlockedUsersQueryService blockedUsersQueryService;

    @Mock
    private FriendshipDao friendshipDao;

    @Mock
    private FriendRequestDao friendRequestDao;

    @Mock
    private AccountClientProxy accountClientProxy;

    @Mock
    private AccountResponseToSearchResultItemConverter accountResponseToSearchResultItemConverter;

    @InjectMocks
    private FriendCandidateSearchService underTest;

    @Mock
    private Friendship friendship;

    @Mock
    private FriendRequest friendRequest;

    @Mock
    private AccountResponse filteredAccountResponse;

    @Mock
    private AccountResponse accountResponse;

    @Mock
    private SearchResultItem searchResultItem;

    @Test
    public void search() {
        given(blockedUsersQueryService.getUserIdsCannotContactWith(USER_ID)).willReturn(List.of(BLOCKED_USER_ID));

        given(friendshipDao.getByUserIdOrFriendId(USER_ID)).willReturn(List.of(friendship));
        given(friendship.getOtherUserId(USER_ID)).willReturn(FRIEND_ID);

        given(friendRequestDao.getBySenderIdOrReceiverId(USER_ID)).willReturn(List.of(friendRequest));
        given(friendRequest.getOtherUserId(USER_ID)).willReturn(RECEIVER_ID);

        given(accountClientProxy.search(QUERY)).willReturn(List.of(filteredAccountResponse, filteredAccountResponse, filteredAccountResponse, accountResponse));
        given(filteredAccountResponse.getUserId())
            .willReturn(BLOCKED_USER_ID)
            .willReturn(FRIEND_ID)
            .willReturn(RECEIVER_ID);
        given(accountResponse.getUserId()).willReturn(UUID.randomUUID());

        given(accountResponseToSearchResultItemConverter.convert(accountResponse)).willReturn(searchResultItem);

        List<SearchResultItem> result = underTest.search(USER_ID, QUERY);

        assertThat(result).containsExactly(searchResultItem);
    }
}