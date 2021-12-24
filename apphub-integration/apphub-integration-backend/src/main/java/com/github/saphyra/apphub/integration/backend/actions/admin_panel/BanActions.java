package com.github.saphyra.apphub.integration.backend.actions.admin_panel;

import com.github.saphyra.apphub.integration.backend.model.account.BanRequest;
import com.github.saphyra.apphub.integration.backend.model.account.BanResponse;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.OneParamRequest;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BanActions {
    public static Response getBanResponse(Language language, UUID accessTokenId, BanRequest request) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.ACCOUNT_BAN_USER));
    }

    public static void ban(Language language, UUID accessTokenId, BanRequest request) {
        Response response = getBanResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static BanResponse getBans(Language language, UUID accessTokenId, UUID testUserId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.ACCOUNT_GET_BANS, "userId", testUserId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(BanResponse.class);
    }

    public static Response getRevokeBanResponse(Language language, UUID accessTokenId, UUID banId, String password) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(password))
            .delete(UrlFactory.create(Endpoints.ACCOUNT_REMOVE_BAN, "banId", banId));
    }

    public static void revokeBan(Language language, UUID accessTokenId, UUID banId, String password) {
        Response response = getRevokeBanResponse(language, accessTokenId, banId, password);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}