package com.github.saphyra.apphub.service.skyxplore.game.process.impl.construction;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.BuildingToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class FinishConstructionService {
    private final AllocationRemovalService allocationRemovalService;
    private final BuildingToModelConverter buildingToModelConverter;
    private final WsMessageSender messageSender;
    private final SurfaceToResponseConverter surfaceToResponseConverter;
    private final PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    void finishConstruction(SyncCache syncCache, Game game, Planet planet, Building building) {
        log.info("Finishing construction...");

        Construction construction = building.getConstruction();

        allocationRemovalService.removeAllocationsAndReservations(syncCache, planet, construction.getConstructionId());

        building.setLevel(building.getLevel() + 1);
        building.setConstruction(null);

        log.info("Upgraded building: {}", building);

        syncCache.deleteGameItem(construction.getConstructionId(), GameItemType.CONSTRUCTION);
        syncCache.saveGameItem(buildingToModelConverter.convert(building, game.getGameId()));

        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED,
            construction.getConstructionId(),
            () -> messageSender.planetQueueItemDeleted(planet.getOwner(), planet.getPlanetId(), construction.getConstructionId())
        );

        Surface surface = planet.findSurfaceByBuildingIdValidated(building.getBuildingId());
        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED,
            surface.getSurfaceId(),
            () -> messageSender.planetSurfaceModified(
                planet.getOwner(),
                planet.getPlanetId(),
                surfaceToResponseConverter.convert(surface)
            )
        );

        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED,
            planet.getPlanetId(),
            () -> messageSender.planetBuildingDetailsModified(
                planet.getOwner(),
                planet.getPlanetId(),
                planetBuildingOverviewQueryService.getBuildingOverview(planet)
            )
        );
    }
}
