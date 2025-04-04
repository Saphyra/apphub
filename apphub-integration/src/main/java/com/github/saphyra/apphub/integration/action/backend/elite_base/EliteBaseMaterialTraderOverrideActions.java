package com.github.saphyra.apphub.integration.action.backend.elite_base;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.EliteBaseEndpoints;
import com.github.saphyra.apphub.integration.structure.api.elite_base.CreateMaterialTraderOverrideRequest;
import io.restassured.response.Response;

import java.util.UUID;

public class EliteBaseMaterialTraderOverrideActions {
    public static Response getCreateMaterialTraderOverrideResponse(int serverPort, UUID accessTokenId, CreateMaterialTraderOverrideRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(serverPort, EliteBaseEndpoints.ELITE_BASE_MATERIAL_TRADER_OVERRIDE_CREATE));
    }

    public static Response getDeleteMaterialTraderOverrideResponse(int serverPort, UUID accessTokenId, UUID stationId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, EliteBaseEndpoints.ELITE_BASE_MATERIAL_TRADER_OVERRIDE_DELETE, "stationId", stationId));
    }

    public static Response getVerifyMaterialTraderOverrideResponse(int serverPort, UUID accessTokenId, UUID stationId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, EliteBaseEndpoints.ELITE_BASE_MATERIAL_TRADER_OVERRIDE_VERIFY, "stationId", stationId));
    }
}
