package com.github.saphyra.apphub.service.skyxplore.data.friend;

import com.github.saphyra.apphub.api.skyxplore.data.server.SkyXploreFriendDataController;
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
    public List<SkyXploreCharacterModel> getFriendCandidates(OneParamRequest<String> queryString, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to get the characters with name {}", accessTokenHeader.getUserId(), queryString.getValue());
        return friendCandidateQueryService.getFriendCandidates(accessTokenHeader.getUserId(), queryString.getValue());
    }

    @Override
    public void createFriendRequest(OneParamRequest<UUID> userId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to add {} as friend", accessTokenHeader.getUserId(), userId.getValue());
        friendRequestCreationService.createFriendRequest(accessTokenHeader.getUserId(), userId.getValue());
    }

    @Override
    public List<SentFriendRequestResponse> getSentFriendRequests(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query his sent friend requests", accessTokenHeader.getUserId());
        return friendRequestQueryService.getSentFriendRequests(accessTokenHeader.getUserId());
    }

    @Override
    public List<IncomingFriendRequestResponse> getIncomingFriendRequests(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query his incoming friend requests", accessTokenHeader.getUserId());
        return friendRequestQueryService.getIncomingFriendRequests(accessTokenHeader.getUserId());
    }

    @Override
    public void cancelFriendRequest(UUID friendRequestId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to cancel friendRequest {}", accessTokenHeader.getUserId(), friendRequestId);
        friendRequestCancelService.cancelFriendRequest(accessTokenHeader.getUserId(), friendRequestId);
    }

    @Override
    public void acceptFriendRequest(UUID friendRequestId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to accept friendRequest {}", accessTokenHeader.getUserId(), friendRequestId);
        friendRequestAcceptService.accept(accessTokenHeader.getUserId(), friendRequestId);
    }

    @Override
    public List<FriendshipResponse> getFriends(AccessTokenHeader accessTokenHeader) {
        return friendshipQueryService.getFriends(accessTokenHeader.getUserId());
    }

    @Override
    public void removeFriend(UUID friendshipId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to remove friendship {}", accessTokenHeader.getUserId(), friendshipId);
        friendshipDeletionService.removeFriendship(friendshipId, accessTokenHeader.getUserId());
    }
}
