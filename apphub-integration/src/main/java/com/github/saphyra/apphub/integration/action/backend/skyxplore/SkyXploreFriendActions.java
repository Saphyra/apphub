package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreDataEndpoints;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreLobbyEndpoints;
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
    public static void setUpFriendship(int serverPort, String senderAccessToken, String friendAccessToken, UUID friendUserId) {
        createFriendRequest(serverPort, senderAccessToken, friendUserId);

        IncomingFriendRequestResponse friendRequest = getIncomingFriendRequests(serverPort, friendAccessToken)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No incoming friendRequests"));

        acceptFriendRequest(serverPort, friendAccessToken, friendRequest.getFriendRequestId());
    }

    public static SentFriendRequestResponse createFriendRequest(int serverPort, String accessToken, UUID userId) {
        Response response = getCreateFriendRequestResponse(serverPort, accessToken, userId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(SentFriendRequestResponse.class);
    }

    public static Response getCreateFriendRequestResponse(int serverPort, String accessToken, UUID userId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(userId))
            .put(UrlFactory.create(serverPort, SkyXploreDataEndpoints.SKYXPLORE_ADD_FRIEND));
    }

    public static List<IncomingFriendRequestResponse> getIncomingFriendRequests(int serverPort, String accessToken) {
        Response response = getIncomingFriendRequestsResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(IncomingFriendRequestResponse[].class))
            .collect(Collectors.toList());
    }

    public static Response getIncomingFriendRequestsResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, SkyXploreDataEndpoints.SKYXPLORE_GET_INCOMING_FRIEND_REQUEST));
    }

    public static FriendshipResponse acceptFriendRequest(int serverPort, String accessToken, UUID friendRequestId) {
        Response response = getAcceptFriendRequestResponse(serverPort, accessToken, friendRequestId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(FriendshipResponse.class);
    }

    public static Response getAcceptFriendRequestResponse(int serverPort, String accessToken, UUID friendRequestId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .post(UrlFactory.create(serverPort, SkyXploreDataEndpoints.SKYXPLORE_ACCEPT_FRIEND_REQUEST, "friendRequestId", friendRequestId));
    }

    public static List<SkyXploreCharacterModel> getFriendCandidates(int serverPort, String accessToken, String queryString) {
        Response response = getFriendCandidatesResponse(serverPort, accessToken, queryString);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(SkyXploreCharacterModel[].class))
            .collect(Collectors.toList());
    }

    public static Response getFriendCandidatesResponse(int serverPort, String accessToken, String queryString) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(queryString))
            .post(UrlFactory.create(serverPort, SkyXploreDataEndpoints.SKYXPLORE_SEARCH_FOR_FRIENDS));
    }

    public static List<SentFriendRequestResponse> getSentFriendRequests(int serverPort, String accessToken) {
        Response response = getSentFriendRequestsResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(SentFriendRequestResponse[].class))
            .collect(Collectors.toList());
    }

    public static Response getSentFriendRequestsResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, SkyXploreDataEndpoints.SKYXPLORE_GET_SENT_FRIEND_REQUEST));
    }

    public static void cancelFriendRequest(int serverPort, String accessToken, UUID friendRequestId) {
        Response response = getCancelFriendRequestResponse(serverPort, accessToken, friendRequestId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCancelFriendRequestResponse(int serverPort, String accessToken, UUID friendRequestId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, SkyXploreDataEndpoints.SKYXPLORE_CANCEL_FRIEND_REQUEST, "friendRequestId", friendRequestId));
    }

    public static List<FriendshipResponse> getFriends(int serverPort, String accessToken) {
        Response response = getFriendsResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return Arrays.stream(response.getBody().as(FriendshipResponse[].class))
            .collect(Collectors.toList());
    }

    public static Response getFriendsResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, SkyXploreDataEndpoints.SKYXPLORE_GET_FRIENDS));
    }

    public static void removeFriend(int serverPort, String accessToken, UUID friendshipId) {
        Response response = getRemoveFriendResponse(serverPort, accessToken, friendshipId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getRemoveFriendResponse(int serverPort, String accessToken, UUID friendshipId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, SkyXploreDataEndpoints.SKYXPLORE_REMOVE_FRIEND, "friendshipId", friendshipId));
    }

    public static List<ActiveFriendResponse> getActiveFriends(int serverPort, String accessToken) {
        Response response = RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, SkyXploreLobbyEndpoints.SKYXPLORE_LOBBY_GET_ACTIVE_FRIENDS));

        assertThat(response.getStatusCode()).isEqualTo(200);
        return Arrays.stream(response.getBody().as(ActiveFriendResponse[].class))
            .collect(Collectors.toList());
    }
}
