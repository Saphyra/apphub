package com.github.saphyra.apphub.integration.action.backend.community;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.community.FriendshipResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class FriendshipActions {
    public static List<FriendshipResponse> getFriendships(UUID accessTokenId) {
        Response response = getFriendshipsResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(FriendshipResponse[].class));
    }

    public static Response getFriendshipsResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.COMMUNITY_GET_FRIENDS));
    }

    public static void deleteFriendship(UUID accessTokenId, UUID friendshipId) {
        Response response = getDeleteFriendshipResponse(accessTokenId, friendshipId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteFriendshipResponse(UUID accessTokenId, UUID friendshipId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.COMMUNITY_DELETE_FRIENDSHIP, "friendshipId", friendshipId));
    }
}
