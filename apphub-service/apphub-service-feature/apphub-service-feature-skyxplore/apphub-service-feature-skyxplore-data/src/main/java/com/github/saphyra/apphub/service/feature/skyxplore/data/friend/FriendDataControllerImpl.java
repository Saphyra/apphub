package com.github.saphyra.apphub.service.feature.skyxplore.data.friend;

import com.github.saphyra.apphub.api.feature.skyxplore.data.server.SkyXploreFriendDataController;
import com.github.saphyra.apphub.api.feature.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.feature.skyxplore.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.api.feature.skyxplore.response.friendship.IncomingFriendRequestResponse;
import com.github.saphyra.apphub.api.feature.skyxplore.response.friendship.SentFriendRequestResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.feature.skyxplore.data.friend.friendship.service.FriendshipDeletionService;
import com.github.saphyra.apphub.service.feature.skyxplore.data.friend.friendship.service.FriendshipQueryService;
import com.github.saphyra.apphub.service.feature.skyxplore.data.friend.request.service.FriendRequestAcceptService;
import com.github.saphyra.apphub.service.feature.skyxplore.data.friend.request.service.FriendRequestCancelService;
import com.github.saphyra.apphub.service.feature.skyxplore.data.friend.request.service.FriendRequestCreationService;
import com.github.saphyra.apphub.service.feature.skyxplore.data.friend.request.service.FriendRequestQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FriendDataControllerImpl implements SkyXploreFriendDataController {
    private final FriendCandidateQueryService friendCandidateQueryService;
    private final FriendRequestCreationService friendRequestCreationService;
    private final FriendRequestQueryService friendRequestQueryService;
    private final FriendRequestCancelService friendRequestCancelService;
    private final FriendRequestAcceptService friendRequestAcceptService;
    private final FriendshipQueryService friendshipQueryService;
    private final FriendshipDeletionService friendshipDeletionService;

    @Override
    public List<SkyXploreCharacterModel> getFriendCandidates(OneParamRequest<String> queryString, AccessToken accessToken) {
        log.info("{} wants to get the characters with name {}", accessToken.getUserId(), queryString.getValue());
        return friendCandidateQueryService.getFriendCandidates(accessToken.getUserId(), queryString.getValue());
    }

    @Override
    public SentFriendRequestResponse createFriendRequest(OneParamRequest<UUID> userId, AccessToken accessToken) {
        log.info("{} wants to add {} as friend", accessToken.getUserId(), userId.getValue());
        return friendRequestCreationService.createFriendRequest(accessToken.getUserId(), userId.getValue());
    }

    @Override
    public List<SentFriendRequestResponse> getSentFriendRequests(AccessToken accessToken) {
        log.info("{} wants to query his sent friend requests", accessToken.getUserId());
        return friendRequestQueryService.getSentFriendRequests(accessToken.getUserId());
    }

    @Override
    public List<IncomingFriendRequestResponse> getIncomingFriendRequests(AccessToken accessToken) {
        log.info("{} wants to query his incoming friend requests", accessToken.getUserId());
        return friendRequestQueryService.getIncomingFriendRequests(accessToken.getUserId());
    }

    @Override
    public void cancelFriendRequest(UUID friendRequestId, AccessToken accessToken) {
        log.info("{} wants to cancel friendRequest {}", accessToken.getUserId(), friendRequestId);
        friendRequestCancelService.cancelFriendRequest(accessToken.getUserId(), friendRequestId);
    }

    @Override
    public FriendshipResponse acceptFriendRequest(UUID friendRequestId, AccessToken accessToken) {
        log.info("{} wants to accept friendRequest {}", accessToken.getUserId(), friendRequestId);
        return friendRequestAcceptService.accept(accessToken.getUserId(), friendRequestId);
    }

    @Override
    public List<FriendshipResponse> getFriends(AccessToken accessToken) {
        return friendshipQueryService.getFriends(accessToken.getUserId());
    }

    @Override
    public void removeFriend(UUID friendshipId, AccessToken accessToken) {
        log.info("{} wants to remove friendship {}", accessToken.getUserId(), friendshipId);
        friendshipDeletionService.removeFriendship(friendshipId, accessToken.getUserId());
    }
}
