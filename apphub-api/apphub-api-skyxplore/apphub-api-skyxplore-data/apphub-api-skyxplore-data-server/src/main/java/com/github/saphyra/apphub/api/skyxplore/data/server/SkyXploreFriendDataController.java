package com.github.saphyra.apphub.api.skyxplore.data.server;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.api.skyxplore.response.friendship.IncomingFriendRequestResponse;
import com.github.saphyra.apphub.api.skyxplore.response.friendship.SentFriendRequestResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface SkyXploreFriendDataController {
    /**
     * Looking for players by the given name, who can be added as friend
     */
    @PostMapping(Endpoints.SKYXPLORE_SEARCH_FOR_FRIENDS)
    List<SkyXploreCharacterModel> getFriendCandidates(@RequestBody OneParamRequest<String> queryString, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PutMapping(Endpoints.SKYXPLORE_ADD_FRIEND)
    SentFriendRequestResponse createFriendRequest(@RequestBody OneParamRequest<UUID> characterId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Pending friend requests sent by the user
     */
    @GetMapping(Endpoints.SKYXPLORE_GET_SENT_FRIEND_REQUEST)
    List<SentFriendRequestResponse> getSentFriendRequests(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Pending friend requests waiting for the user's decision
     */
    @GetMapping(Endpoints.SKYXPLORE_GET_INCOMING_FRIEND_REQUEST)
    List<IncomingFriendRequestResponse> getIncomingFriendRequests(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Can be called by the sender and the receiver to remove the friend request
     */
    @DeleteMapping(Endpoints.SKYXPLORE_CANCEL_FRIEND_REQUEST)
    void cancelFriendRequest(@PathVariable("friendRequestId") UUID friendRequestId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.SKYXPLORE_ACCEPT_FRIEND_REQUEST)
    FriendshipResponse acceptFriendRequest(@PathVariable("friendRequestId") UUID friendRequestId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.SKYXPLORE_GET_FRIENDS)
    List<FriendshipResponse> getFriends(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.SKYXPLORE_REMOVE_FRIEND)
    void removeFriend(@PathVariable("friendshipId") UUID friendshipId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
