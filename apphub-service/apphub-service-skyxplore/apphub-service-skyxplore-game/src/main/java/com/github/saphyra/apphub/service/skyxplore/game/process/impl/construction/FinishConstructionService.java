package com.github.saphyra.apphub.service.skyxplore.game.process.impl.construction;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
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

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class FinishConstructionService {
    private final AllocationRemovalService allocationRemovalService;
    private final BuildingToModelConverter buildingToModelConverter;
    private final WsMessageSender messageSender;
    private final SurfaceToResponseConverter surfaceToResponseConverter;
    private final PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    void finishConstruction(SyncCache syncCache, GameData gameData, UUID location, Building building, Construction construction) {
        log.info("Finishing construction...");

        UUID ownerId = gameData.getPlanets()
            .get(location)
            .getOwner();

        allocationRemovalService.removeAllocationsAndReservations(syncCache, gameData, location, ownerId, construction.getConstructionId());

        building.increaseLevel();

        gameData.getConstructions()
            .deleteByConstructionId(construction.getConstructionId());

        log.info("Upgraded building: {}", building);

        syncCache.deleteGameItem(construction.getConstructionId(), GameItemType.CONSTRUCTION);
        syncCache.saveGameItem(buildingToModelConverter.convert(gameData.getGameId(), building));

        syncCache.addMessage(
            ownerId,
            WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED,
            construction.getConstructionId(),
            () -> messageSender.planetQueueItemDeleted(
                ownerId,
                location,
                construction.getConstructionId()
            )
        );

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(building.getSurfaceId());
        syncCache.addMessage(
            ownerId,
            WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED,
            surface.getSurfaceId(),
            () -> messageSender.planetSurfaceModified(
                ownerId,
                location,
                surfaceToResponseConverter.convert(gameData, surface)
            )
        );

        syncCache.addMessage(
            ownerId,
            WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED,
            location,
            () -> messageSender.planetBuildingDetailsModified(
                ownerId,
                location,
                planetBuildingOverviewQueryService.getBuildingOverview(gameData, location)
            )
        );
    }
}
