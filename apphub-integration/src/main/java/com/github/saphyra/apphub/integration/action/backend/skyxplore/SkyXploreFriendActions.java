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
    public static void setUpFriendship(int serverPort, UUID senderAccessTokenId, UUID friendAccessTokenId, UUID friendUserId) {
        createFriendRequest(serverPort, senderAccessTokenId, friendUserId);

        IncomingFriendRequestResponse friendRequest = getIncomingFriendRequests(serverPort, friendAccessTokenId)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No incoming friendRequests"));

        acceptFriendRequest(serverPort, friendAccessTokenId, friendRequest.getFriendRequestId());
    }

    public static SentFriendRequestResponse createFriendRequest(int serverPort, UUID accessTokenId, UUID userId) {
        Response response = getCreateFriendRequestResponse(serverPort, accessTokenId, userId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(SentFriendRequestResponse.class);
    }

    public static Response getCreateFriendRequestResponse(int serverPort, UUID accessTokenId, UUID userId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(userId))
            .put(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_ADD_FRIEND));
    }

    public static List<IncomingFriendRequestResponse> getIncomingFriendRequests(int serverPort, UUID accessTokenId) {
        Response response = getIncomingFriendRequestsResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(IncomingFriendRequestResponse[].class))
            .collect(Collectors.toList());
    }

    public static Response getIncomingFriendRequestsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GET_INCOMING_FRIEND_REQUEST));
    }

    public static FriendshipResponse acceptFriendRequest(int serverPort, UUID accessTokenId, UUID friendRequestId) {
        Response response = getAcceptFriendRequestResponse(serverPort, accessTokenId, friendRequestId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(FriendshipResponse.class);
    }

    public static Response getAcceptFriendRequestResponse(int serverPort, UUID accessTokenId, UUID friendRequestId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_ACCEPT_FRIEND_REQUEST, "friendRequestId", friendRequestId));
    }

    public static List<SkyXploreCharacterModel> getFriendCandidates(int serverPort, UUID accessTokenId, String queryString) {
        Response response = getFriendCandidatesResponse(serverPort, accessTokenId, queryString);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(SkyXploreCharacterModel[].class))
            .collect(Collectors.toList());
    }

    public static Response getFriendCandidatesResponse(int serverPort, UUID accessTokenId, String queryString) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(queryString))
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_SEARCH_FOR_FRIENDS));
    }

    public static List<SentFriendRequestResponse> getSentFriendRequests(int serverPort, UUID accessTokenId) {
        Response response = getSentFriendRequestsResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(SentFriendRequestResponse[].class))
            .collect(Collectors.toList());
    }

    public static Response getSentFriendRequestsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GET_SENT_FRIEND_REQUEST));
    }

    public static void cancelFriendRequest(int serverPort, UUID accessTokenId, UUID friendRequestId) {
        Response response = getCancelFriendRequestResponse(serverPort, accessTokenId, friendRequestId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCancelFriendRequestResponse(int serverPort, UUID accessTokenId, UUID friendRequestId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_CANCEL_FRIEND_REQUEST, "friendRequestId", friendRequestId));
    }

    public static List<FriendshipResponse> getFriends(int serverPort, UUID accessTokenId) {
        Response response = getFriendsResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return Arrays.stream(response.getBody().as(FriendshipResponse[].class))
            .collect(Collectors.toList());
    }

    public static Response getFriendsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GET_FRIENDS));
    }

    public static void removeFriend(int serverPort, UUID accessTokenId, UUID friendshipId) {
        Response response = getRemoveFriendResponse(serverPort, accessTokenId, friendshipId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getRemoveFriendResponse(int serverPort, UUID accessTokenId, UUID friendshipId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_REMOVE_FRIEND, "friendshipId", friendshipId));
    }

    public static List<ActiveFriendResponse> getActiveFriends(int serverPort, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_LOBBY_GET_ACTIVE_FRIENDS));

        assertThat(response.getStatusCode()).isEqualTo(200);
        return Arrays.stream(response.getBody().as(ActiveFriendResponse[].class))
            .collect(Collectors.toList());
    }
}
