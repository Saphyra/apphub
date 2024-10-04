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
    public static FriendRequestResponse createFriendRequest(int serverPort, UUID accessTokenId, UUID friendUserId) {
        Response response = getCreateFriendRequestResponse(serverPort, accessTokenId, friendUserId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(FriendRequestResponse.class);
    }

    public static Response getCreateFriendRequestResponse(int serverPort, UUID accessTokenId, UUID friendUserId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(friendUserId))
            .put(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_FRIEND_REQUEST_CREATE));
    }

    public static List<FriendRequestResponse> getSentFriendRequests(int serverPort, UUID accessTokenId) {
        Response response = getSentFriendRequestsResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(FriendRequestResponse[].class));
    }

    public static Response getSentFriendRequestsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_GET_SENT_FRIEND_REQUESTS));
    }

    public static List<FriendRequestResponse> getReceivedFriendRequests(int serverPort, UUID accessTokenId) {
        Response response = getReceivedFriendRequeestsResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(FriendRequestResponse[].class));
    }

    public static Response getReceivedFriendRequeestsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_GET_RECEIVED_FRIEND_REQUESTS));
    }

    public static FriendshipResponse acceptFriendRequest(int serverPort, UUID accessToken, UUID friendRequestId) {
        Response response = RequestFactory.createAuthorizedRequest(accessToken)
            .post(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_FRIEND_REQUEST_ACCEPT, "friendRequestId", friendRequestId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(FriendshipResponse.class);
    }

    public static List<SearchResultItem> search(int serverPort, UUID accessTokenId, String query) {
        Response response = getSearchResponse(serverPort, accessTokenId, query);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(SearchResultItem[].class));
    }

    public static Response getSearchResponse(int serverPort, UUID accessTokenId, String query) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(query))
            .post(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_FRIEND_REQUEST_SEARCH));
    }

    public static void deleteFriendRequest(int serverPort, UUID accessTokenId, UUID friendRequestId) {
        Response response = getDeleteFriendRequestResponse(serverPort, accessTokenId, friendRequestId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteFriendRequestResponse(int serverPort, UUID accessTokenId, UUID friendRequestId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_FRIEND_REQUEST_DELETE, "friendRequestId", friendRequestId));
    }

    public static Response getAcceptFriendRequestResponse(int serverPort, UUID accessTokenId, UUID friendRequestId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_FRIEND_REQUEST_ACCEPT, "friendRequestId", friendRequestId));
    }
}
