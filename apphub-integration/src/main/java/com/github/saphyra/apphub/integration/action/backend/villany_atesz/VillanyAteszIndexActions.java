package com.github.saphyra.apphub.integration.action.backend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class VillanyAteszIndexActions {
    public static Integer getTotalValue(UUID accessTokenId) {
        Response response = getTotalValueResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().jsonPath().getInt("value");
    }

    public static Response getTotalValueResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.VILLANY_ATESZ_INDEX_TOTAL_VALUE));
    }
}
