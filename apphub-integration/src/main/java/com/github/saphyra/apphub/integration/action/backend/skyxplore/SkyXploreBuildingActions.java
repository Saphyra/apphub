package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.skyxplore.SurfaceResponse;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreBuildingActions {
    public static SurfaceResponse constructNewBuilding(Language language, UUID accessTokenId, UUID planetId, UUID surfaceId, String dataId) {
        Response response = getConstructNewBuildingResponse(language, accessTokenId, planetId, surfaceId, dataId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(SurfaceResponse.class);
    }

    public static Response getConstructNewBuildingResponse(Language language, UUID accessTokenId, UUID planetId, UUID surfaceId, String dataId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(dataId))
            .put(UrlFactory.create(Endpoints.SKYXPLORE_BUILDING_CONSTRUCT_NEW, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("surfaceId", surfaceId))));
    }

    public static SurfaceResponse upgradeBuilding(Language language, UUID accessTokenId, UUID planetId, UUID buildingId) {
        Response response = getUpgradeBuildingResponse(language, accessTokenId, planetId, buildingId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(SurfaceResponse.class);
    }

    public static Response getUpgradeBuildingResponse(Language language, UUID accessTokenId, UUID planetId, UUID buildingId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_BUILDING_UPGRADE, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("buildingId", buildingId))));
    }

    public static SurfaceResponse cancelConstruction(Language language, UUID accessTokenId, UUID planetId, UUID buildingId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .delete(UrlFactory.create(Endpoints.SKYXPLORE_BUILDING_CANCEL_CONSTRUCTION, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("buildingId", buildingId))));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(SurfaceResponse.class);
    }

    public static SurfaceResponse deconstructBuilding(Language language, UUID accessTokenId, UUID planetId, UUID buildingId) {
        Response response = getDeconstructBuildingResponse(language, accessTokenId, planetId, buildingId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(SurfaceResponse.class);
    }

    public static Response getDeconstructBuildingResponse(Language language, UUID accessTokenId, UUID planetId, UUID buildingId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_BUILDING_DECONSTRUCT, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("buildingId", buildingId))));
    }

    public static SurfaceResponse cancelDeconstruction(Language language, UUID accessTokenId, UUID planetId, UUID buildingId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .delete(UrlFactory.create(Endpoints.SKYXPLORE_BUILDING_CANCEL_DECONSTRUCTION, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("buildingId", buildingId))));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(SurfaceResponse.class);
    }
}
