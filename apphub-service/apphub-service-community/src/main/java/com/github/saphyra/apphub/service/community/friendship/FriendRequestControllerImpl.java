package com.github.saphyra.apphub.service.community.friendship;

import com.github.saphyra.apphub.api.community.model.response.SearchResultItem;
import com.github.saphyra.apphub.api.community.model.response.friend_request.FriendRequestResponse;
import com.github.saphyra.apphub.api.community.model.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.api.community.server.FriendRequestController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.community.friendship.service.AcceptFriendRequestService;
import com.github.saphyra.apphub.service.community.friendship.service.FriendCandidateSearchService;
import com.github.saphyra.apphub.service.community.friendship.service.FriendRequestCreationService;
import com.github.saphyra.apphub.service.community.friendship.service.FriendRequestDeletionService;
import com.github.saphyra.apphub.service.community.friendship.service.FriendRequestQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class FriendRequestControllerImpl implements FriendRequestController {
    private final FriendCandidateSearchService friendCandidateSearchService;
    private final FriendRequestQueryService friendRequestQueryService;
    private final FriendRequestCreationService friendRequestCreationService;
    private final FriendRequestDeletionService friendRequestDeletionService;
    private final AcceptFriendRequestService acceptFriendRequestService;

    @Override
    public List<SearchResultItem> search(OneParamRequest<String> query, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query users to send FriendRequest based on text {}", accessTokenHeader.getUserId(), query.getValue());
        return friendCandidateSearchService.search(accessTokenHeader.getUserId(), query.getValue());
    }

    @Override
    public List<FriendRequestResponse> getSentFriendRequests(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know his sent FriendRequest", accessTokenHeader.getUserId());
        return friendRequestQueryService.getSentFriendRequests(accessTokenHeader.getUserId());
    }

    @Override
    public List<FriendRequestResponse> getReceivedFriendRequests(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know his received friend requests", accessTokenHeader.getUserId());
        return friendRequestQueryService.getReceivedFriendRequests(accessTokenHeader.getUserId());
    }

    @Override
    public FriendRequestResponse create(OneParamRequest<UUID> friendUserId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to send a FriendRequest to {}", accessTokenHeader.getUserId(), friendUserId.getValue());
        return friendRequestCreationService.create(accessTokenHeader.getUserId(), friendUserId.getValue());
    }

    @Override
    public void delete(UUID friendRequestId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete FriendRequest {}", accessTokenHeader, friendRequestId);
        friendRequestDeletionService.delete(accessTokenHeader.getUserId(), friendRequestId);
    }

    @Override
    public FriendshipResponse accept(UUID friendRequestId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to accept FriendRequest {}", accessTokenHeader, friendRequestId);
        return acceptFriendRequestService.accept(accessTokenHeader.getUserId(), friendRequestId);
    }
}
