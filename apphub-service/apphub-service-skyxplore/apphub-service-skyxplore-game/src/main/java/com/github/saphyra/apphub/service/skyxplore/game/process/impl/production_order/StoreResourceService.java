package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.AllocatedResourceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ReservedStorageToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.StoredResourceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
class StoreResourceService {
    private final ReservedStorageToModelConverter reservedStorageToModelConverter;
    private final AllocatedResourceToModelConverter allocatedResourceToModelConverter;
    private final StoredResourceToModelConverter storedResourceToModelConverter;
    private final WsMessageSender messageSender;
    private final PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    void storeResource(SyncCache syncCache, Game game, Planet planet, ReservedStorage reservedStorage, AllocatedResource allocatedResource, int amount) {
        log.info("ProductionOrder finished. Storing {} of {}", amount, reservedStorage.getDataId());

        StoredResource storedResource = planet.getStorageDetails()
            .getStoredResources()
            .get(reservedStorage.getDataId());

        log.info("Before update: {}, {}, {}", storedResource, allocatedResource, reservedStorage);

        if (nonNull(allocatedResource)) {
            allocatedResource.increaseAmount(amount);
            syncCache.saveGameItem(allocatedResourceToModelConverter.convert(allocatedResource, game.getGameId()));
        }
        storedResource.increaseAmount(amount);
        reservedStorage.decreaseAmount(amount);

        log.info("After update: {}, {}, {}", storedResource, allocatedResource, reservedStorage);

        syncCache.saveGameItem(reservedStorageToModelConverter.convert(reservedStorage, game.getGameId()));
        syncCache.saveGameItem(storedResourceToModelConverter.convert(storedResource, game.getGameId()));

        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED,
            planet.getPlanetId(),
            () -> messageSender.planetStorageModified(
                planet.getOwner(),
                planet.getPlanetId(),
                planetStorageOverviewQueryService.getStorage(planet)
            )
        );
    }
}
