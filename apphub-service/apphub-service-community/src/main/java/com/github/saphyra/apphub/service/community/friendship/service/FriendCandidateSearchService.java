package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.api.community.model.response.SearchResultItem;
import com.github.saphyra.apphub.service.community.blacklist.service.BlockedUsersQueryService;
import com.github.saphyra.apphub.service.community.common.AccountClientProxy;
import com.github.saphyra.apphub.service.community.common.AccountResponseToSearchResultItemConverter;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.FriendshipDao;
import com.github.saphyra.apphub.service.community.friendship.dao.request.FriendRequestDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendCandidateSearchService {
    private final BlockedUsersQueryService blockedUsersQueryService;
    private final FriendshipDao friendshipDao;
    private final FriendRequestDao friendRequestDao;
    private final AccountClientProxy accountClientProxy;
    private final AccountResponseToSearchResultItemConverter accountResponseToSearchResultItemConverter;

    public List<SearchResultItem> search(UUID userId, String query) {
        List<UUID> excludedUsers = Stream.of(
            blockedUsersQueryService.getUserIdsCannotContactWith(userId),
            friendshipDao.getByUserIdOrFriendId(userId).stream().map(friendship -> friendship.getOtherUserId(userId)).collect(Collectors.toList()),
            friendRequestDao.getBySenderIdOrReceiverId(userId).stream().map(friendRequest -> friendRequest.getOtherUserId(userId)).collect(Collectors.toList())
        )
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        return accountClientProxy.search(query)
            .stream()
            .filter(accountResponse -> !excludedUsers.contains(accountResponse.getUserId()))
            .map(accountResponseToSearchResultItemConverter::convert)
            .collect(Collectors.toList());
    }
}
