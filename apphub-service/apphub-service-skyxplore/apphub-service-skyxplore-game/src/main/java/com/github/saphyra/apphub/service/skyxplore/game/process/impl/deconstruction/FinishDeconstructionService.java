package com.github.saphyra.apphub.service.skyxplore.game.process.impl.deconstruction;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinishDeconstructionService {
    private final GameDataProxy gameDataProxy;
    private final WsMessageSender messageSender;
    private final SurfaceToResponseConverter surfaceToResponseConverter;
    private final PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    public void finishDeconstruction(GameData gameData, UUID location, SyncCache syncCache, Deconstruction deconstruction) {
        log.info("Finishing deconstruction...");

        UUID surfaceId = gameData.getBuildings()
            .findByBuildingId(deconstruction.getExternalReference())
            .getSurfaceId();

        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(surfaceId);

        gameData.getBuildings()
            .deleteByBuildingId(deconstruction.getExternalReference());

        gameDataProxy.deleteItem(deconstruction.getDeconstructionId(), GameItemType.DECONSTRUCTION);
        gameDataProxy.deleteItem(deconstruction.getExternalReference(), GameItemType.BUILDING);

        Planet planet = gameData.getPlanets()
            .get(location);
        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED,
            deconstruction.getDeconstructionId(),
            () -> messageSender.planetQueueItemDeleted(
                planet.getOwner(),
                location,
                deconstruction.getDeconstructionId()
            )
        );

        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED,
            surface.getSurfaceId(),
            () -> messageSender.planetSurfaceModified(
                planet.getOwner(),
                location,
                surfaceToResponseConverter.convert(gameData, surface)
            )
        );

        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED,
            location,
            () -> messageSender.planetBuildingDetailsModified(
                planet.getOwner(),
                location,
                planetBuildingOverviewQueryService.getBuildingOverview(gameData, planet.getPlanetId())
            )
        );
    }
}
