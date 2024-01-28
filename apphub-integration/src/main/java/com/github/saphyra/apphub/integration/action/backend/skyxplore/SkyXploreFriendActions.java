package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.ActiveFriendResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.FriendshipResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.IncomingFriendRequestResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SentFriendRequestResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreFriendActions {
    public static void setUpFriendship(UUID senderAccessTokenId, UUID friendAccessTokenId, UUID friendUserId) {
        createFriendRequest(senderAccessTokenId, friendUserId);

        IncomingFriendRequestResponse friendRequest = getIncomingFriendRequests(friendAccessTokenId)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No incoming friendRequests"));

        acceptFriendRequest(friendAccessTokenId, friendRequest.getFriendRequestId());
    }

    public static SentFriendRequestResponse createFriendRequest(UUID accessTokenId, UUID userId) {
        Response response = getCreateFriendRequestResponse(accessTokenId, userId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(SentFriendRequestResponse.class);
    }

    public static Response getCreateFriendRequestResponse(UUID accessTokenId, UUID userId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(userId))
            .put(UrlFactory.create(Endpoints.SKYXPLORE_ADD_FRIEND));
    }

    public static List<IncomingFriendRequestResponse> getIncomingFriendRequests(UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_GET_INCOMING_FRIEND_REQUEST));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(IncomingFriendRequestResponse[].class))
            .collect(Collectors.toList());
    }

    public static FriendshipResponse acceptFriendRequest(UUID accessTokenId, UUID friendRequestId) {
        Response response = getAcceptFriendRequestResponse(accessTokenId, friendRequestId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(FriendshipResponse.class);
    }

    public static Response getAcceptFriendRequestResponse(UUID accessTokenId, UUID friendRequestId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_ACCEPT_FRIEND_REQUEST, "friendRequestId", friendRequestId));
    }

    public static List<SkyXploreCharacterModel> getFriendCandidates(UUID accessTokenId, String queryString) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(queryString))
            .post(UrlFactory.create(Endpoints.SKYXPLORE_SEARCH_FOR_FRIENDS));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(SkyXploreCharacterModel[].class))
            .collect(Collectors.toList());
    }

    public static List<SentFriendRequestResponse> getSentFriendRequests(UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_GET_SENT_FRIEND_REQUEST));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(SentFriendRequestResponse[].class))
            .collect(Collectors.toList());
    }

    public static void cancelFriendRequest(UUID accessTokenId, UUID friendRequestId) {
        Response response = getCancelFriendRequestResponse(accessTokenId, friendRequestId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCancelFriendRequestResponse(UUID accessTokenId, UUID friendRequestId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.SKYXPLORE_CANCEL_FRIEND_REQUEST, "friendRequestId", friendRequestId));
    }

    public static List<FriendshipResponse> getFriends(UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_GET_FRIENDS));

        assertThat(response.getStatusCode()).isEqualTo(200);
        return Arrays.stream(response.getBody().as(FriendshipResponse[].class))
            .collect(Collectors.toList());
    }

    public static void removeFriend(UUID accessTokenId, UUID friendshipId) {
        Response response = getRemoveFriendResponse(accessTokenId, friendshipId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getRemoveFriendResponse(UUID accessTokenId, UUID friendshipId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.SKYXPLORE_REMOVE_FRIEND, "friendshipId", friendshipId));
    }

    public static List<ActiveFriendResponse> getActiveFriends(UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_GET_ACTIVE_FRIENDS));

        assertThat(response.getStatusCode()).isEqualTo(200);
        return Arrays.stream(response.getBody().as(ActiveFriendResponse[].class))
            .collect(Collectors.toList());
    }
}
