package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class StoredResourceAggregator {
    private final StoredResourceFactory storedResourceFactory;

    /**
     * Collects the StoredResources into one item
     */
    List<StoredResource> aggregate(GameProgressDiff progressDiff, GameData gameData, Map<UUID, List<StoredResource>> availableResources) {
        return availableResources.values()
            .stream()
            .map(storedResources -> aggregate(progressDiff, gameData, storedResources))
            .toList();
    }

    private StoredResource aggregate(GameProgressDiff progressDiff, GameData gameData, List<StoredResource> storedResources) {
        if (storedResources.size() == 1) {
            return storedResources.get(0);
        }

        int totalResources = storedResources.stream()
            .mapToInt(StoredResource::getAmount)
            .sum();

        gameData.getStoredResources()
            .removeAll(storedResources);
        storedResources.forEach(storedResource -> progressDiff.delete(storedResource.getStoredResourceId(), GameItemType.STORED_RESOURCE));

        StoredResource base = storedResources.get(0);

        return storedResourceFactory.save(progressDiff, gameData, base.getLocation(), base.getDataId(), totalResources, base.getContainerId(), base.getContainerType());
    }
}
