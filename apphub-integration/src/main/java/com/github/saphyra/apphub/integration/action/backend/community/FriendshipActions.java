package com.github.saphyra.apphub.integration.action.backend.community;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.community.FriendshipResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class FriendshipActions {
    public static List<FriendshipResponse> getFriendships(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.COMMUNITY_GET_FRIENDS));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(FriendshipResponse[].class));
    }

    public static void deleteFriendship(Language language, UUID accessTokenId, UUID friendshipId) {
        Response response = getDeleteFriendshipResponse(language, accessTokenId, friendshipId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteFriendshipResponse(Language language, UUID accessTokenId, UUID friendshipId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .delete(UrlFactory.create(Endpoints.COMMUNITY_DELETE_FRIENDSHIP, "friendshipId", friendshipId));
    }
}
