package com.github.saphyra.apphub.service.skyxplore.game.process.impl.terraformation;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.SurfaceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class FinishTerraformationService {
    private final AllocationRemovalService allocationRemovalService;
    private final SurfaceToModelConverter surfaceToModelConverter;
    private final WsMessageSender messageSender;
    private final SurfaceToResponseConverter surfaceToResponseConverter;
    private final PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    void finishTerraformation(SyncCache syncCache, GameData gameData, UUID location, Construction terraformation) {
        log.info("Finishing terraformation...");

        UUID ownerId = gameData.getPlanets()
            .get(location)
            .getOwner();

        allocationRemovalService.removeAllocationsAndReservations(syncCache, gameData, location, ownerId, terraformation.getConstructionId());

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(terraformation.getExternalReference());

        surface.setSurfaceType(SurfaceType.valueOf(terraformation.getData()));

        gameData.getConstructions()
            .deleteByConstructionId(terraformation.getConstructionId());

        log.info("Terraformed surface: {}", surface);

        syncCache.deleteGameItem(terraformation.getConstructionId(), GameItemType.CONSTRUCTION);
        syncCache.saveGameItem(surfaceToModelConverter.convert(gameData.getGameId(), surface));

        syncCache.addMessage(
            ownerId,
            WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED,
            terraformation.getConstructionId(),
            () -> messageSender.planetQueueItemDeleted(
                ownerId,
                location,
                terraformation.getConstructionId()
            )
        );

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
