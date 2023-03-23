package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.StoredResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.AllocatedResourceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ReservedStorageToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.StoredResourceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
    private final StoredResourceFactory storedResourceFactory;

    void storeResource(SyncCache syncCache, GameData gameData, UUID location, UUID ownerId, ReservedStorage reservedStorage, AllocatedResource allocatedResource, int amount) {
        log.info("ProductionOrder finished. Storing {} of {}", amount, reservedStorage.getDataId());

        StoredResource storedResource = gameData.getStoredResources()
            .findByLocationAndDataId(location, reservedStorage.getDataId())
            .orElseGet(() -> storedResourceFactory.create(location, reservedStorage.getDataId()));

        log.info("Before update: {}, {}, {}", storedResource, allocatedResource, reservedStorage);

        if (nonNull(allocatedResource)) {
            allocatedResource.increaseAmount(amount);
            syncCache.saveGameItem(allocatedResourceToModelConverter.convert(gameData.getGameId(), allocatedResource));
        }
        storedResource.increaseAmount(amount);
        reservedStorage.decreaseAmount(amount);

        log.info("After update: {}, {}, {}", storedResource, allocatedResource, reservedStorage);

        syncCache.saveGameItem(reservedStorageToModelConverter.convert(gameData.getGameId(), reservedStorage));
        syncCache.saveGameItem(storedResourceToModelConverter.convert(gameData.getGameId(), storedResource));

        syncCache.addMessage(
            ownerId,
            WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED,
            location,
            () -> messageSender.planetStorageModified(
                ownerId,
                location,
                planetStorageOverviewQueryService.getStorage(gameData, location)
            )
        );
    }
}
