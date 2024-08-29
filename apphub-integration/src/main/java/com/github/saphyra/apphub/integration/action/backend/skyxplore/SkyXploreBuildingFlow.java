package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SurfaceResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreBuildingFlow {
    public static void constructBuilding(int serverPort, UUID accessTokenId, UUID planetId, String surfaceType, String dataId) {
        UUID surfaceId = SkyXploreSurfaceActions.findEmptySurfaceId(serverPort, accessTokenId, planetId, surfaceType);

        SkyXploreBuildingActions.constructNewBuilding(serverPort, accessTokenId, planetId, surfaceId, dataId);

        //Resume game
        SkyXploreGameActions.setPaused(serverPort, accessTokenId, false);

        AwaitilityWrapper.getWithWait(
                () -> SkyXplorePlanetActions.getPlanetOverview(serverPort, accessTokenId, planetId),
                por -> findSurfaceBySurfaceId(por.getSurfaces(), surfaceId)
                    .map(SurfaceResponse::getBuilding)
                    .filter(surfaceBuildingResponse -> isNull(surfaceBuildingResponse.getConstruction()))
                    .isPresent(),
                120,
                5
            )
            .orElseThrow(() -> new RuntimeException("Construction is not finished."));

        //Pause game again
        SkyXploreGameActions.setPaused(serverPort, accessTokenId, true);
    }

    private static Optional<SurfaceResponse> findSurfaceBySurfaceId(List<SurfaceResponse> surfaces, UUID surfaceId) {
        assertThat(surfaces).isNotNull();

        return surfaces.stream()
            .filter(surfaceResponse -> surfaceResponse.getSurfaceId().equals(surfaceId))
            .findAny();
    }
}
