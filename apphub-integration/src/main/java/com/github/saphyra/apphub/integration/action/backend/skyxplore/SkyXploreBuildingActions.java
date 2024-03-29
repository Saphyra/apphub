package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreBuildingActions {
    public static void constructNewBuilding(UUID accessTokenId, UUID planetId, UUID surfaceId, String dataId) {
        Response response = getConstructNewBuildingResponse(accessTokenId, planetId, surfaceId, dataId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getConstructNewBuildingResponse(UUID accessTokenId, UUID planetId, UUID surfaceId, String dataId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(dataId))
            .put(UrlFactory.create(Endpoints.SKYXPLORE_BUILDING_CONSTRUCT_NEW, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("surfaceId", surfaceId))));
    }

    public static void upgradeBuilding(UUID accessTokenId, UUID planetId, UUID buildingId) {
        Response response = getUpgradeBuildingResponse(accessTokenId, planetId, buildingId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getUpgradeBuildingResponse(UUID accessTokenId, UUID planetId, UUID buildingId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_BUILDING_UPGRADE, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("buildingId", buildingId))));
    }

    public static void cancelConstruction(UUID accessTokenId, UUID planetId, UUID buildingId) {
        Response response = getCancelConstructionResponse(accessTokenId, planetId, buildingId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCancelConstructionResponse(UUID accessTokenId, UUID planetId, UUID buildingId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.SKYXPLORE_BUILDING_CANCEL_CONSTRUCTION, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("buildingId", buildingId))));
    }

    public static void deconstructBuilding(UUID accessTokenId, UUID planetId, UUID buildingId) {
        Response response = getDeconstructBuildingResponse(accessTokenId, planetId, buildingId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeconstructBuildingResponse(UUID accessTokenId, UUID planetId, UUID buildingId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_BUILDING_DECONSTRUCT, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("buildingId", buildingId))));
    }

    public static void cancelDeconstruction(UUID accessTokenId, UUID planetId, UUID buildingId) {
        Response response = getCancelDeconstructionResponse(accessTokenId, planetId, buildingId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCancelDeconstructionResponse(UUID accessTokenId, UUID planetId, UUID buildingId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.SKYXPLORE_BUILDING_CANCEL_DECONSTRUCTION, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("buildingId", buildingId))));
    }
}
