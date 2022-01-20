package com.github.saphyra.apphub.service.skyxplore.game.tick.planet.processor;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.BuildingToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.GameItemCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.MessageCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class FinishConstructionService {
    private final TickCache tickCache;
    private final BuildingToModelConverter buildingToModelConverter;
    private final SurfaceToResponseConverter surfaceToResponseConverter;
    private final WsMessageSender messageSender;

    void finishConstruction(UUID gameId, Planet planet, Surface surface) {
        Building building = surface.getBuilding();
        building.setLevel(building.getLevel() + 1);
        Construction construction = building.getConstruction();

        GameItemCache gameItemCache = tickCache.get(gameId)
            .getGameItemCache();
        gameItemCache.save(buildingToModelConverter.convert(building, gameId));
        gameItemCache.delete(construction.getConstructionId(), GameItemType.CONSTRUCTION);

        MessageCache messageCache = tickCache.get(gameId)
            .getMessageCache();
        messageCache.add(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED,
            construction.getConstructionId(),
            () -> messageSender.planetQueueItemDeleted(planet.getOwner(), planet.getPlanetId(), construction.getConstructionId())
        );
        messageCache.add(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED,
            surface.getSurfaceId(),
            () -> messageSender.planetSurfaceModified(planet.getOwner(), planet.getPlanetId(), surfaceToResponseConverter.convert(surface))
        );
    }
}
