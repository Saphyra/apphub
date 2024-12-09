package com.github.saphyra.apphub.integration.action.backend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreGameEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.game.PlanetOverviewResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.game.PlanetStorageResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.game.SurfaceResponse;
import io.restassured.response.Response;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class SkyXplorePlanetActions {
    public static PlanetOverviewResponse getPlanetOverview(int serverPort, UUID accessTokenId, UUID planetId) {
        Response response = getPlanetOverviewResponse(serverPort, accessTokenId, planetId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(PlanetOverviewResponse.class);
    }

    public static Response getPlanetOverviewResponse(int serverPort, UUID accessTokenId, UUID planetId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_GET_OVERVIEW, "planetId", planetId));
    }

    public static Response getRenamePlanetResponse(int serverPort, UUID accessTokenId, UUID planetId, String planetName) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(planetName))
            .post(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_RENAME, "planetId", planetId));
    }

    public static void renamePlanet(int serverPort, UUID accessTokenId, UUID planetId, String planetName) {
        Response response = getRenamePlanetResponse(serverPort, accessTokenId, planetId, planetName);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static List<SurfaceResponse> getSurfaces(int serverPort, UUID accessTokenId, UUID planetId) {
        return getPlanetOverview(serverPort, accessTokenId, planetId)
            .getSurfaces();
    }

    public static PlanetStorageResponse getStorageOverview(int serverPort, UUID accessTokenId, UUID planetId) {
        return getPlanetOverview(serverPort, accessTokenId, planetId)
            .getStorage();
    }

    public static SurfaceResponse findSurfaceBySurfaceId(int serverPort, UUID accessTokenId, UUID planetId, UUID surfaceId) {
        return findSurfaceBySurfaceId(getSurfaces(serverPort, accessTokenId, planetId), surfaceId)
            .orElseThrow(() -> new RuntimeException("Surface %s not found on planet %s".formatted(surfaceId, planetId)));
    }

    public static Optional<SurfaceResponse> findSurfaceBySurfaceId(List<SurfaceResponse> surfaces, UUID surfaceId) {
        return Optional.ofNullable(surfaces)
            .flatMap(surfaceResponses -> surfaceResponses.stream().filter(surfaceResponse -> surfaceResponse.getSurfaceId().equals(surfaceId)).findAny());
    }

    public static UUID findEmptySurface(int serverPort, UUID accessTokenId, UUID planetId, String surfaceType) {
        return SkyXplorePlanetActions.getSurfaces(serverPort, accessTokenId, planetId)
            .stream()
            .filter(surfaceResponse -> isNull(surfaceResponse.getConstructionArea()))
            .filter(surfaceResponse -> surfaceResponse.getSurfaceType().equals(surfaceType))
            .findFirst()
            .map(SurfaceResponse::getSurfaceId)
            .orElseThrow(() -> new RuntimeException("Empty Desert not found on planet " + planetId));
    }

    public static UUID findOccupiedSurfaceId(int serverPort, UUID accessTokenId, UUID planetId) {
        return SkyXplorePlanetActions.getSurfaces(serverPort, accessTokenId, planetId)
            .stream()
            .filter(surfaceResponse -> !isNull(surfaceResponse.getConstructionArea()))
            .findFirst()
            .map(SurfaceResponse::getSurfaceId)
            .orElseThrow(() -> new RuntimeException("Empty Desert not found on planet " + planetId));
    }
}
