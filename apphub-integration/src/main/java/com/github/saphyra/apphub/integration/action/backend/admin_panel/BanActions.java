package com.github.saphyra.apphub.integration.action.backend.admin_panel;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.admin_panel.MarkUserForDeletionRequest;
import com.github.saphyra.apphub.integration.structure.api.user.BanRequest;
import com.github.saphyra.apphub.integration.structure.api.user.BanResponse;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BanActions {
    public static Response getBanResponse(UUID accessTokenId, BanRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.ACCOUNT_BAN_USER));
    }

    public static void ban(UUID accessTokenId, BanRequest request) {
        Response response = getBanResponse(accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static BanResponse getBans(UUID accessTokenId, UUID testUserId) {
        Response response = getGetBansResponse(accessTokenId, testUserId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(BanResponse.class);
    }

    public static Response getGetBansResponse(UUID accessTokenId, UUID testUserId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.ACCOUNT_GET_BANS, "userId", testUserId));
    }

    public static Response getRevokeBanResponse(UUID accessTokenId, UUID banId, String password) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(password))
            .delete(UrlFactory.create(Endpoints.ACCOUNT_REMOVE_BAN, "banId", banId));
    }

    public static void revokeBan(UUID accessTokenId, UUID banId, String password) {
        Response response = getRevokeBanResponse(accessTokenId, banId, password);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static BanResponse markUserForDeletion(UUID accessTokenId, UUID deletedUserId, MarkUserForDeletionRequest request) {
        Response response = getMarkForDeletionResponse(accessTokenId, deletedUserId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(BanResponse.class);
    }

    public static Response getMarkForDeletionResponse(UUID accessTokenId, UUID deletedUserId, MarkUserForDeletionRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .delete(UrlFactory.create(Endpoints.ACCOUNT_MARK_FOR_DELETION, "userId", deletedUserId));
    }

    public static BanResponse unmarkUserForDeletion(UUID accessTokenId, UUID deletedUserId) {
        Response response = getUnmarkUserForDeletionResponse(accessTokenId, deletedUserId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(BanResponse.class);
    }

    public static Response getUnmarkUserForDeletionResponse(UUID accessTokenId, UUID deletedUserId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(Endpoints.ACCOUNT_UNMARK_FOR_DELETION, "userId", deletedUserId));
    }

    public static Response getSearchResponse(UUID accessTokenId, String query) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(query))
            .post(UrlFactory.create(Endpoints.ACCOUNT_BAN_SEARCH));
    }
}
