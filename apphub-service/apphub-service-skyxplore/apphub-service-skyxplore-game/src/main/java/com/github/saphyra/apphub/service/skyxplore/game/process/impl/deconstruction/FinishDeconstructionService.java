package com.github.saphyra.apphub.service.skyxplore.game.process.impl.deconstruction;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinishDeconstructionService {
    private final GameDataProxy gameDataProxy;
    private final WsMessageSender messageSender;
    private final SurfaceToResponseConverter surfaceToResponseConverter;
    private final PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    public void finishDeconstruction(SyncCache syncCache, Planet planet, Deconstruction deconstruction) {
        log.info("Finishing deconstruction...");

        Surface surface = planet.findSurfaceByBuildingIdValidated(deconstruction.getExternalReference());
        Building building = surface.getBuilding();

        surface.setBuilding(null);
        gameDataProxy.deleteItem(deconstruction.getDeconstructionId(), GameItemType.DECONSTRUCTION);
        gameDataProxy.deleteItem(building.getBuildingId(), GameItemType.BUILDING);

        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED,
            deconstruction.getDeconstructionId(),
            () -> messageSender.planetQueueItemDeleted(planet.getOwner(), planet.getPlanetId(), deconstruction.getDeconstructionId())
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
