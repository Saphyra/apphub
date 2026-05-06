package com.github.saphyra.apphub.integration.action.backend.admin_panel;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.UserEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.admin_panel.MarkUserForDeletionRequest;
import com.github.saphyra.apphub.integration.structure.api.user.BanRequest;
import com.github.saphyra.apphub.integration.structure.api.user.BanResponse;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BanActions {
    public static Response getBanResponse(int serverPort, String accessToken, BanRequest request) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(request)
            .put(UrlFactory.create(serverPort, UserEndpoints.ACCOUNT_BAN_USER));
    }

    public static void ban(int serverPort, String accessToken, BanRequest request) {
        Response response = getBanResponse(serverPort, accessToken, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static BanResponse getBans(int serverPort, String accessToken, UUID testUserId) {
        Response response = getGetBansResponse(serverPort, accessToken, testUserId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(BanResponse.class);
    }

    public static Response getGetBansResponse(int serverPort, String accessToken, UUID testUserId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, UserEndpoints.ACCOUNT_GET_BANS, "userId", testUserId));
    }

    public static Response getRevokeBanResponse(int serverPort, String accessToken, UUID banId, String password) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(password))
            .delete(UrlFactory.create(serverPort, UserEndpoints.ACCOUNT_REVOKE_BAN, "banId", banId));
    }

    public static void revokeBan(int serverPort, String accessToken, UUID banId, String password) {
        Response response = getRevokeBanResponse(serverPort, accessToken, banId, password);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static BanResponse markUserForDeletion(int serverPort, String accessToken, UUID deletedUserId, MarkUserForDeletionRequest request) {
        Response response = getMarkForDeletionResponse(serverPort, accessToken, deletedUserId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(BanResponse.class);
    }

    public static Response getMarkForDeletionResponse(int serverPort, String accessToken, UUID deletedUserId, MarkUserForDeletionRequest request) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(request)
            .delete(UrlFactory.create(serverPort, UserEndpoints.ACCOUNT_MARK_FOR_DELETION, "userId", deletedUserId));
    }

    public static BanResponse unmarkUserForDeletion(int serverPort, String accessToken, UUID deletedUserId) {
        Response response = getUnmarkUserForDeletionResponse(serverPort, accessToken, deletedUserId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(BanResponse.class);
    }

    public static Response getUnmarkUserForDeletionResponse(int serverPort, String accessToken, UUID deletedUserId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .post(UrlFactory.create(serverPort, UserEndpoints.ACCOUNT_UNMARK_FOR_DELETION, "userId", deletedUserId));
    }

    public static Response getSearchResponse(int serverPort, String accessToken, String query) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(query))
            .post(UrlFactory.create(serverPort, UserEndpoints.ACCOUNT_BAN_SEARCH));
    }
}
