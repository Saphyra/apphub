package com.github.saphyra.apphub.api.community.server;

import com.github.saphyra.apphub.api.community.model.response.SearchResultItem;
import com.github.saphyra.apphub.api.community.model.response.friend_request.FriendRequestResponse;
import com.github.saphyra.apphub.api.community.model.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
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

public interface FriendRequestController {
    @PostMapping(Endpoints.COMMUNITY_FRIEND_REQUEST_SEARCH)
    List<SearchResultItem> search(@RequestBody OneParamRequest<String> query, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.COMMUNITY_GET_SENT_FRIEND_REQUESTS)
    List<FriendRequestResponse> getSentFriendRequests(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.COMMUNITY_GET_RECEIVED_FRIEND_REQUESTS)
    List<FriendRequestResponse> getReceivedFriendRequests(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PutMapping(Endpoints.COMMUNITY_FRIEND_REQUEST_CREATE)
    FriendRequestResponse create(@RequestBody OneParamRequest<UUID> friendUserId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.COMMUNITY_FRIEND_REQUEST_DELETE)
    void delete(@PathVariable("friendRequestId") UUID friendRequestId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.COMMUNITY_FRIEND_REQUEST_ACCEPT)
    FriendshipResponse accept(@PathVariable("friendRequestId") UUID friendRequestId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
