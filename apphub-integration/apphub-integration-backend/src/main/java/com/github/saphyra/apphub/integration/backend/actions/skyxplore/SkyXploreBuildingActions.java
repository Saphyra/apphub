package com.github.saphyra.apphub.integration.backend.actions.skyxplore;

import com.github.saphyra.apphub.integration.backend.model.skyxplore.ConstructionResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SurfaceBuildingResponse;
import com.github.saphyra.apphub.integration.common.framework.BiWrapper;
import com.github.saphyra.apphub.integration.common.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.OneParamRequest;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreBuildingActions {
    public static SurfaceBuildingResponse constructNewBuilding(Language language, UUID accessTokenId, UUID planetId, UUID surfaceId, String dataId) {
        Response response = getConstructNewBuildingResponse(language, accessTokenId, planetId, surfaceId, dataId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(SurfaceBuildingResponse.class);
    }

    public static Response getConstructNewBuildingResponse(Language language, UUID accessTokenId, UUID planetId, UUID surfaceId, String dataId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(dataId))
            .put(UrlFactory.create(Endpoints.SKYXPLORE_BUILDING_CONSTRUCT_NEW, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("surfaceId", surfaceId))));
    }

    public static ConstructionResponse upgradeBuilding(Language language, UUID accessTokenId, UUID planetId, UUID buildingId) {
        Response response = getUpgradeBuildingResponse(language, accessTokenId, planetId, buildingId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(ConstructionResponse.class);
    }

    public static Response getUpgradeBuildingResponse(Language language, UUID accessTokenId, UUID planetId, UUID buildingId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_BUILDING_UPGRADE, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("buildingId", buildingId))));
    }

    public static void cancelConstruction(Language language, UUID accessTokenId, UUID planetId, UUID buildingId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .delete(UrlFactory.create(Endpoints.SKYXPLORE_BUILDING_CANCEL_CONSTRUCTION, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("buildingId", buildingId))));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
