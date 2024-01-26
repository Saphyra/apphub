package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetOverviewResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SurfaceResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreBuildingFlow {
    public static void constructBuilding(UUID accessTokenId, UUID planetId, String surfaceType, String dataId) {
        UUID surfaceId = SkyXploreSurfaceActions.findEmptySurfaceId(accessTokenId, planetId, surfaceType);

        SkyXploreBuildingActions.constructNewBuilding(accessTokenId, planetId, surfaceId, dataId);

        //Resume game
        SkyXploreGameActions.setPaused(accessTokenId, false);

        PlanetOverviewResponse planetOverviewResponse = AwaitilityWrapper.getWithWait(
                () -> SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planetId),
                por -> findSurfaceBySurfaceId(por.getSurfaces(), surfaceId)
                    .map(SurfaceResponse::getBuilding)
                    .filter(surfaceBuildingResponse -> isNull(surfaceBuildingResponse.getConstruction()))
                    .isPresent(),
                120,
                5
            )
            .orElseThrow(() -> new RuntimeException("Construction is not finished."));

        //Pause game again
        SkyXploreGameActions.setPaused(accessTokenId, true);
    }

    private static Optional<SurfaceResponse> findSurfaceBySurfaceId(List<SurfaceResponse> surfaces, UUID surfaceId) {
        assertThat(surfaces).isNotNull();

        return surfaces.stream()
            .filter(surfaceResponse -> surfaceResponse.getSurfaceId().equals(surfaceId))
            .findAny();
    }
}
