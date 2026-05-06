package com.github.saphyra.apphub.integration.action.backend.community;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.CommunityEndpoints;
import com.github.saphyra.apphub.integration.structure.api.community.FriendshipResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class FriendshipActions {
    public static List<FriendshipResponse> getFriendships(int serverPort, String accessToken) {
        Response response = getFriendshipsResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(FriendshipResponse[].class));
    }

    public static Response getFriendshipsResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_GET_FRIENDS));
    }

    public static void deleteFriendship(int serverPort, String accessToken, UUID friendshipId) {
        Response response = getDeleteFriendshipResponse(serverPort, accessToken, friendshipId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteFriendshipResponse(int serverPort, String accessToken, UUID friendshipId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_DELETE_FRIENDSHIP, "friendshipId", friendshipId));
    }
}
