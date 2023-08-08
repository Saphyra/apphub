package com.github.saphyra.apphub.integration.action.backend.community;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
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
    public static FriendRequestResponse createFriendRequest(Language language, UUID accessTokenId, UUID friendUserId) {
        Response response = getCreateFriendRequestResponse(language, accessTokenId, friendUserId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(FriendRequestResponse.class);
    }

    public static Response getCreateFriendRequestResponse(Language language, UUID accessTokenId, UUID friendUserId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(friendUserId))
            .put(UrlFactory.create(Endpoints.COMMUNITY_FRIEND_REQUEST_CREATE));
    }

    public static List<FriendRequestResponse> getSentFriendRequests(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.COMMUNITY_GET_SENT_FRIEND_REQUESTS));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(FriendRequestResponse[].class));
    }

    public static List<FriendRequestResponse> getReceivedFriendRequests(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.COMMUNITY_GET_RECEIVED_FRIEND_REQUESTS));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(FriendRequestResponse[].class));
    }

    public static FriendshipResponse acceptFriendRequest(Language language, UUID accessToken, UUID friendRequestId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessToken)
            .post(UrlFactory.create(Endpoints.COMMUNITY_FRIEND_REQUEST_ACCEPT, "friendRequestId", friendRequestId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(FriendshipResponse.class);
    }

    public static List<SearchResultItem> search(Language language, UUID accessTokenId, String query) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(query))
            .post(UrlFactory.create(Endpoints.COMMUNITY_FRIEND_REQUEST_SEARCH));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(SearchResultItem[].class));
    }

    public static void deleteFriendRequest(Language language, UUID accessTokenId, UUID friendRequestId) {
        Response response = getDeleteFriendRequestResponse(language, accessTokenId, friendRequestId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteFriendRequestResponse(Language language, UUID accessTokenId, UUID friendRequestId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .delete(UrlFactory.create(Endpoints.COMMUNITY_FRIEND_REQUEST_DELETE, "friendRequestId", friendRequestId));
    }

    public static Response getAcceptFriendRequestResponse(Language language, UUID accessTokenId, UUID friendRequestId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .post(UrlFactory.create(Endpoints.COMMUNITY_FRIEND_REQUEST_ACCEPT, "friendRequestId", friendRequestId));
    }
}
