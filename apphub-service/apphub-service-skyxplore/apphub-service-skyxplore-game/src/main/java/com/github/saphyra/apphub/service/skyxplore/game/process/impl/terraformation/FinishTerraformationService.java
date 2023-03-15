package com.github.saphyra.apphub.service.skyxplore.game.process.impl.terraformation;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
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

@Component
@RequiredArgsConstructor
@Slf4j
class FinishTerraformationService {
    private final AllocationRemovalService allocationRemovalService;
    private final SurfaceToModelConverter surfaceToModelConverter;
    private final WsMessageSender messageSender;
    private final SurfaceToResponseConverter surfaceToResponseConverter;
    private final PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    void finishTerraformation(SyncCache syncCache, Game game, Planet planet, Surface surface) {
        log.info("Finishing terraformation...");

        Construction construction = surface.getTerraformation();

        allocationRemovalService.removeAllocationsAndReservations(syncCache, planet, construction.getConstructionId());

        surface.setSurfaceType(SurfaceType.valueOf(construction.getData()));
        surface.setTerraformation(null);

        log.info("Terraformed surface: {}", surface);

        syncCache.deleteGameItem(construction.getConstructionId(), GameItemType.CONSTRUCTION);
        syncCache.saveGameItem(surfaceToModelConverter.convert(surface, game.getGameId()));

        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED,
            construction.getConstructionId(),
            () -> messageSender.planetQueueItemDeleted(planet.getOwner(), planet.getPlanetId(), construction.getConstructionId())
        );

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
