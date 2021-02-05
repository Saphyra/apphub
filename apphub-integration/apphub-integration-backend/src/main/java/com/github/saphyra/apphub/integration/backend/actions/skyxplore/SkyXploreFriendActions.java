package com.github.saphyra.apphub.integration.backend.actions.skyxplore;

import com.github.saphyra.apphub.integration.backend.model.skyxplore.FriendshipResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.IncomingFriendRequestResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SentFriendRequestResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.OneParamRequest;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreFriendActions {
    public static void createFriendRequest(Language language, UUID accessTokenId, UUID userId) {
        Response response = getCreateFriendRequestResponse(language, accessTokenId, userId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateFriendRequestResponse(Language language, UUID accessTokenId, UUID userId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(userId))
            .put(UrlFactory.create(Endpoints.SKYXPLORE_ADD_FRIEND));
    }

    public static List<IncomingFriendRequestResponse> getIncomingFriendRequests(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_GET_INCOMING_FRIEND_REQUEST));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(IncomingFriendRequestResponse[].class))
            .collect(Collectors.toList());
    }

    public static void acceptFriendRequest(Language language, UUID accessTokenId, UUID friendRequestId) {
        Response response = getAcceptFriendRequestResponse(language, accessTokenId, friendRequestId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getAcceptFriendRequestResponse(Language language, UUID accessTokenId, UUID friendRequestId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_ACCEPT_FRIEND_REQUEST, "friendRequestId", friendRequestId));
    }

    public static List<SkyXploreCharacterModel> getFriendCandidates(Language language, UUID accessTokenId, String queryString) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(queryString))
            .post(UrlFactory.create(Endpoints.SKYXPLORE_SEARCH_FOR_FRIENDS));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(SkyXploreCharacterModel[].class))
            .collect(Collectors.toList());
    }

    public static List<SentFriendRequestResponse> getSentFriendRequests(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_GET_SENT_FRIEND_REQUEST));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(SentFriendRequestResponse[].class))
            .collect(Collectors.toList());
    }

    public static Response getCancelFriendRequestResponse(Language language, UUID accessTokenId, UUID friendRequestId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .delete(UrlFactory.create(Endpoints.SKYXPLORE_CANCEL_FRIEND_REQUEST, "friendRequestId", friendRequestId));
    }

    public static List<FriendshipResponse> getFriends(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_GET_FRIENDS));

        assertThat(response.getStatusCode()).isEqualTo(200);
        return Arrays.stream(response.getBody().as(FriendshipResponse[].class))
            .collect(Collectors.toList());
    }

    public static Response getRemoveFriendResponse(Language language, UUID accessTokenId, UUID friendshipId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .delete(UrlFactory.create(Endpoints.SKYXPLORE_REMOVE_FRIEND, "friendshipId", friendshipId));
    }
}
