package com.github.saphyra.apphub.integration.action.backend.community;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.CommunityEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.community.FriendRequestResponse;
import com.github.saphyra.apphub.integration.structure.api.community.FriendshipResponse;
import com.github.saphyra.apphub.integration.structure.api.community.SearchResultItem;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class FriendRequestActions {
    public static FriendRequestResponse createFriendRequest(int serverPort, String accessToken, UUID friendUserId) {
        Response response = getCreateFriendRequestResponse(serverPort, accessToken, friendUserId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(FriendRequestResponse.class);
    }

    public static Response getCreateFriendRequestResponse(int serverPort, String accessToken, UUID friendUserId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(friendUserId))
            .put(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_FRIEND_REQUEST_CREATE));
    }

    public static List<FriendRequestResponse> getSentFriendRequests(int serverPort, String accessToken) {
        Response response = getSentFriendRequestsResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(FriendRequestResponse[].class));
    }

    public static Response getSentFriendRequestsResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_GET_SENT_FRIEND_REQUESTS));
    }

    public static List<FriendRequestResponse> getReceivedFriendRequests(int serverPort, String accessToken) {
        Response response = getReceivedFriendRequeestsResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(FriendRequestResponse[].class));
    }

    public static Response getReceivedFriendRequeestsResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_GET_RECEIVED_FRIEND_REQUESTS));
    }

    public static FriendshipResponse acceptFriendRequest(int serverPort, String accessToken, UUID friendRequestId) {
        Response response = RequestFactory.createAuthorizedRequest(accessToken)
            .post(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_FRIEND_REQUEST_ACCEPT, "friendRequestId", friendRequestId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(FriendshipResponse.class);
    }

    public static List<SearchResultItem> search(int serverPort, String accessToken, String query) {
        Response response = getSearchResponse(serverPort, accessToken, query);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(SearchResultItem[].class));
    }

    public static Response getSearchResponse(int serverPort, String accessToken, String query) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(query))
            .post(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_FRIEND_REQUEST_SEARCH));
    }

    public static void deleteFriendRequest(int serverPort, String accessToken, UUID friendRequestId) {
        Response response = getDeleteFriendRequestResponse(serverPort, accessToken, friendRequestId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteFriendRequestResponse(int serverPort, String accessToken, UUID friendRequestId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_FRIEND_REQUEST_DELETE, "friendRequestId", friendRequestId));
    }

    public static Response getAcceptFriendRequestResponse(int serverPort, String accessToken, UUID friendRequestId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .post(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_FRIEND_REQUEST_ACCEPT, "friendRequestId", friendRequestId));
    }
}
