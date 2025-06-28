package com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
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

    public StoredResource save(GameProgressDiff gameProgressDiff, GameData gameData, UUID location, String dataId, UUID containerId, ContainerType containerType) {
        return save(gameProgressDiff, gameData, location, dataId, 0, containerId, containerType);
    }

    public StoredResource save(GameProgressDiff gameProgressDiff, GameData gameData, UUID location, String dataId, int amount, UUID containerId, ContainerType containerType) {
        return save(gameProgressDiff, gameData, location, dataId, amount, containerId, containerType, null);
    }

    public StoredResource save(GameProgressDiff gameProgressDiff, GameData gameData, UUID location, String dataId, int amount, UUID containerId, ContainerType containerType, UUID allocatedBy) {
        StoredResource result = create(location, dataId, amount, containerId, containerType, allocatedBy);

        gameData.getStoredResources()
            .add(result);
        gameProgressDiff.save(storedResourceConverter.toModel(gameData.getGameId(), result));

        log.info("Saved: {}", result);

        return result;
    }

    public StoredResource create(UUID location, String dataId, int amount, UUID containerId, ContainerType containerType, UUID allocatedBy) {
        return StoredResource.builder()
            .storedResourceId(idGenerator.randomUuid())
            .location(location)
            .dataId(dataId)
            .amount(amount)
            .containerId(containerId)
            .containerType(containerType)
            .allocatedBy(allocatedBy)
            .build();
    }
}
