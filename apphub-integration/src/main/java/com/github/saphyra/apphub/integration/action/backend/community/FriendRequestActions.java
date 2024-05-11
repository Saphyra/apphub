package com.github.saphyra.apphub.integration.action.backend.community;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
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
    public static FriendRequestResponse createFriendRequest(UUID accessTokenId, UUID friendUserId) {
        Response response = getCreateFriendRequestResponse(accessTokenId, friendUserId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(FriendRequestResponse.class);
    }

    public static Response getCreateFriendRequestResponse(UUID accessTokenId, UUID friendUserId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(friendUserId))
            .put(UrlFactory.create(Endpoints.COMMUNITY_FRIEND_REQUEST_CREATE));
    }

    public static List<FriendRequestResponse> getSentFriendRequests(UUID accessTokenId) {
        Response response = getSentFriendRequestsResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(FriendRequestResponse[].class));
    }

    public static Response getSentFriendRequestsResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.COMMUNITY_GET_SENT_FRIEND_REQUESTS));
    }

    public static List<FriendRequestResponse> getReceivedFriendRequests(UUID accessTokenId) {
        Response response = getReceivedFriendRequeestsResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(FriendRequestResponse[].class));
    }

    public static Response getReceivedFriendRequeestsResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.COMMUNITY_GET_RECEIVED_FRIEND_REQUESTS));
    }

    public static FriendshipResponse acceptFriendRequest(UUID accessToken, UUID friendRequestId) {
        Response response = RequestFactory.createAuthorizedRequest(accessToken)
            .post(UrlFactory.create(Endpoints.COMMUNITY_FRIEND_REQUEST_ACCEPT, "friendRequestId", friendRequestId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(FriendshipResponse.class);
    }

    public static List<SearchResultItem> search(UUID accessTokenId, String query) {
        Response response = getSearchResponse(accessTokenId, query);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(SearchResultItem[].class));
    }

    public static Response getSearchResponse(UUID accessTokenId, String query) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(query))
            .post(UrlFactory.create(Endpoints.COMMUNITY_FRIEND_REQUEST_SEARCH));
    }

    public static void deleteFriendRequest(UUID accessTokenId, UUID friendRequestId) {
        Response response = getDeleteFriendRequestResponse(accessTokenId, friendRequestId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteFriendRequestResponse(UUID accessTokenId, UUID friendRequestId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.COMMUNITY_FRIEND_REQUEST_DELETE, "friendRequestId", friendRequestId));
    }

    public static Response getAcceptFriendRequestResponse(UUID accessTokenId, UUID friendRequestId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(Endpoints.COMMUNITY_FRIEND_REQUEST_ACCEPT, "friendRequestId", friendRequestId));
    }
}
