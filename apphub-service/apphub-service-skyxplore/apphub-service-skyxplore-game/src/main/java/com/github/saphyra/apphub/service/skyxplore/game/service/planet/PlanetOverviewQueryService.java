package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetOverviewResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.PlanetPopulationOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.priority.PriorityQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueFacade;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.BuildingsSummaryQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceResponseQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class PlanetOverviewQueryService {
    private final GameDao gameDao;
    private final PlanetPopulationOverviewQueryService planetPopulationOverviewQueryService;
    private final PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;
    private final SurfaceResponseQueryService surfaceResponseQueryService;
    private final PlanetStorageOverviewQueryService planetStorageOverviewQueryService;
    private final PriorityQueryService priorityQueryService;
    private final QueueFacade queueFacade;
    private final BuildingsSummaryQueryService buildingsSummaryQueryService;

    PlanetOverviewResponse getOverview(UUID userId, UUID planetId) {
        Planet planet = gameDao.findByUserIdValidated(userId)
            .getData()
            .getPlanets()
            .findByIdValidated(planetId);

        return PlanetOverviewResponse.builder()
            .planetName(getPlanetName(userId, planet))
            .surfaces(surfaceResponseQueryService.getSurfaceOfPlanet(userId, planetId))
            .storage(planetStorageOverviewQueryService.getStorage(userId, planetId))
            .population(planetPopulationOverviewQueryService.getPopulationOverview(userId, planetId))
            .buildings(planetBuildingOverviewQueryService.getBuildingOverview(userId, planetId))
            .buildingsSummary(buildingsSummaryQueryService.getBuildingsSummary(userId, planetId))
            .priorities(priorityQueryService.getPriorities(userId, planetId))
            .queue(queueFacade.getQueueOfPlanet(userId, planetId))
            .build();
    }

    private String getPlanetName(UUID userId, Planet planet) {
        return planet.getCustomNames().getOrDefault(userId, planet.getDefaultName());
    }
}
