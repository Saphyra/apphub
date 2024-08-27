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
    public static void constructNewBuilding(int serverPort, UUID accessTokenId, UUID planetId, UUID surfaceId, String dataId) {
        Response response = getConstructNewBuildingResponse(serverPort, accessTokenId, planetId, surfaceId, dataId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getConstructNewBuildingResponse(int serverPort, UUID accessTokenId, UUID planetId, UUID surfaceId, String dataId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(dataId))
            .put(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_BUILDING_CONSTRUCT_NEW, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("surfaceId", surfaceId))));
    }

    public static void upgradeBuilding(int serverPort, UUID accessTokenId, UUID planetId, UUID buildingId) {
        Response response = getUpgradeBuildingResponse(serverPort, accessTokenId, planetId, buildingId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getUpgradeBuildingResponse(int serverPort, UUID accessTokenId, UUID planetId, UUID buildingId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_BUILDING_UPGRADE, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("buildingId", buildingId))));
    }

    public static void cancelConstruction(int serverPort, UUID accessTokenId, UUID planetId, UUID buildingId) {
        Response response = getCancelConstructionResponse(serverPort, accessTokenId, planetId, buildingId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCancelConstructionResponse(int serverPort, UUID accessTokenId, UUID planetId, UUID buildingId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_BUILDING_CANCEL_CONSTRUCTION, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("buildingId", buildingId))));
    }

    public static void deconstructBuilding(int serverPort, UUID accessTokenId, UUID planetId, UUID buildingId) {
        Response response = getDeconstructBuildingResponse(serverPort, accessTokenId, planetId, buildingId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeconstructBuildingResponse(int serverPort, UUID accessTokenId, UUID planetId, UUID buildingId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_BUILDING_DECONSTRUCT, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("buildingId", buildingId))));
    }

    public static void cancelDeconstruction(int serverPort, UUID accessTokenId, UUID planetId, UUID buildingId) {
        Response response = getCancelDeconstructionResponse(serverPort, accessTokenId, planetId, buildingId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCancelDeconstructionResponse(int serverPort, UUID accessTokenId, UUID planetId, UUID buildingId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_BUILDING_CANCEL_DECONSTRUCTION, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("buildingId", buildingId))));
    }
}
