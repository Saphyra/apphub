package com.github.saphyra.apphub.integration.action.backend.elite_base;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.EliteBaseEndpoints;
import com.github.saphyra.apphub.integration.structure.api.elite_base.merit_farm.MiningMeritFarmRequest;
import io.restassured.response.Response;

public class EliteBaseMeritMinerActions {
    public static Response getMeritFarmLocations(int serverPort, String accessToken, String power, MiningMeritFarmRequest request) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(request)
            .post(UrlFactory.create(serverPort, EliteBaseEndpoints.ELITE_BASE_MERIT_FARM_GET_MINE_LOCATIONS, "power", power));
    }
}
