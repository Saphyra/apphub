package com.github.saphyra.apphub.integration.action.backend.elite_base;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.EliteBaseEndpoints;
import com.github.saphyra.apphub.integration.structure.api.elite_base.MaterialType;
import io.restassured.response.Response;

import java.util.Map;
import java.util.UUID;

public class EliteBaseNearestActions {
    public static Response getNearestMaterialTradersResponse(int serverPort, UUID accessTokenId, UUID starId, MaterialType materialType, int page) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(
                serverPort,
                EliteBaseEndpoints.ELITE_BASE_NEAREST_MATERIAL_TRADERS,
                Map.of(
                    "starId", starId,
                    "materialType", materialType,
                    "page", page
                )
            ));
    }
}
