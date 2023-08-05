package com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StoredResourceFactory {
    private final IdGenerator idGenerator;
    private final StoredResourceConverter storedResourceConverter;

    public StoredResource create(SyncCache syncCache, GameData gameData, UUID location, String dataId) {
        return create(syncCache, gameData, location, dataId, 0);
    }

    public StoredResource create(SyncCache syncCache, GameData gameData, UUID location, String dataId, int amount) {
        StoredResource result = create(location, dataId, amount);

        gameData.getStoredResources()
            .add(result);
        syncCache.saveGameItem(storedResourceConverter.toModel(gameData.getGameId(), result));

        return result;
    }

    public StoredResource create(UUID location, String dataId, int amount) {
        return StoredResource.builder()
            .storedResourceId(idGenerator.randomUuid())
            .location(location)
            .dataId(dataId)
            .amount(amount)
            .build();
    }
}
