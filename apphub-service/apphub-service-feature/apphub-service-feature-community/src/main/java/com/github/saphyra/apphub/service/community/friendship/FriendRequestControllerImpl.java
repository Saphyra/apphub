package com.github.saphyra.apphub.service.community.friendship;

import com.github.saphyra.apphub.api.feature.community.model.response.SearchResultItem;
import com.github.saphyra.apphub.api.feature.community.model.response.friend_request.FriendRequestResponse;
import com.github.saphyra.apphub.api.feature.community.model.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.api.feature.community.server.FriendRequestController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
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
    public List<SearchResultItem> search(OneParamRequest<String> query, AccessToken accessToken) {
        log.info("{} wants to query users to send FriendRequest based on text {}", accessToken.getUserId(), query.getValue());
        return friendCandidateSearchService.search(accessToken.getUserId(), query.getValue());
    }

    @Override
    public List<FriendRequestResponse> getSentFriendRequests(AccessToken accessToken) {
        log.info("{} wants to know his sent FriendRequest", accessToken.getUserId());
        return friendRequestQueryService.getSentFriendRequests(accessToken.getUserId());
    }

    @Override
    public List<FriendRequestResponse> getReceivedFriendRequests(AccessToken accessToken) {
        log.info("{} wants to know his received friend requests", accessToken.getUserId());
        return friendRequestQueryService.getReceivedFriendRequests(accessToken.getUserId());
    }

    @Override
    public FriendRequestResponse create(OneParamRequest<UUID> friendUserId, AccessToken accessToken) {
        log.info("{} wants to send a FriendRequest to {}", accessToken.getUserId(), friendUserId.getValue());
        return friendRequestCreationService.create(accessToken.getUserId(), friendUserId.getValue());
    }

    @Override
    public void delete(UUID friendRequestId, AccessToken accessToken) {
        log.info("{} wants to delete FriendRequest {}", accessToken.getUserId(), friendRequestId);
        friendRequestDeletionService.delete(accessToken.getUserId(), friendRequestId);
    }

    @Override
    public FriendshipResponse accept(UUID friendRequestId, AccessToken accessToken) {
        log.info("{} wants to accept FriendRequest {}", accessToken.getUserId(), friendRequestId);
        return acceptFriendRequestService.accept(accessToken.getUserId(), friendRequestId);
    }
}
