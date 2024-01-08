package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetOverviewResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetStorageResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SurfaceResponse;
import io.restassured.response.Response;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class SkyXplorePlanetActions {
    public static PlanetOverviewResponse getPlanetOverview(UUID accessTokenId, UUID planetId) {
        Response response = getPlanetOverviewResponse(accessTokenId, planetId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(PlanetOverviewResponse.class);
    }

    private static Response getPlanetOverviewResponse(UUID accessTokenId, UUID planetId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_GET_OVERVIEW, "planetId", planetId));
    }

    public static Response getRenamePlanetResponse(Language language, UUID accessTokenId, UUID planetId, String planetName) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(planetName))
            .post(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_RENAME, "planetId", planetId));
    }

    public static void renamePlanet(Language language, UUID accessTokenId, UUID planetId, String planetName) {
        Response response = getRenamePlanetResponse(language, accessTokenId, planetId, planetName);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static List<SurfaceResponse> getSurfaces(UUID accessTokenId, UUID planetId) {
        return getPlanetOverview(accessTokenId, planetId)
            .getSurfaces();
    }

    public static PlanetStorageResponse getStorageOverview(UUID accessTokenId, UUID planetId) {
        return getPlanetOverview(accessTokenId, planetId)
            .getStorage();
    }

    public static Optional<SurfaceResponse> findSurfaceBySurfaceId(List<SurfaceResponse> surfaces, UUID surfaceId) {
        return Optional.ofNullable(surfaces)
            .flatMap(surfaceResponses -> surfaceResponses.stream().filter(surfaceResponse -> surfaceResponse.getSurfaceId().equals(surfaceId)).findAny());
    }

    public static Optional<SurfaceResponse> findSurfaceByBuildingId(List<SurfaceResponse> surfaces, UUID buildingId) {
        return Optional.ofNullable(surfaces)
            .flatMap(surfaceResponses -> surfaceResponses.stream()
                .filter(surfaceResponse -> !isNull(surfaceResponse.getBuilding()))
                .filter(surfaceResponse -> surfaceResponse.getBuilding().getBuildingId().equals(buildingId))
                .findAny()
            );
    }
}
