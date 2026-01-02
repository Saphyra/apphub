package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.core.TestBase;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreDataEndpoints;
import io.restassured.response.Response;
import tools.jackson.core.type.TypeReference;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreGameDataActions {
    public static Response getGameDateResponse(int serverPort, UUID accessTokenId, String dataId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, SkyXploreDataEndpoints.SKYXPLORE_GET_ITEM_DATA, "dataId", dataId));
    }

    public static List<String> getAvailableConstructionAreas(int serverPort, UUID accessTokenId, String surfaceType) {
        Response response = getAvailableConstructionAreasResponse(serverPort, accessTokenId, surfaceType);

        assertThat(response.getStatusCode()).isEqualTo(200);

        TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {
        };

        return Arrays.stream(response.getBody().as(Object[].class))
            .map(o -> TestBase.OBJECT_MAPPER_WRAPPER.convertValue(o, typeRef))
            .map(map -> map.get("id").toString())
            .collect(Collectors.toList());
    }

    public static Response getAvailableConstructionAreasResponse(int serverPort, UUID accessTokenId, String surfaceType) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, SkyXploreDataEndpoints.SKYXPLORE_DATA_CONSTRUCTION_AREAS, "surfaceType", surfaceType));
    }
}
