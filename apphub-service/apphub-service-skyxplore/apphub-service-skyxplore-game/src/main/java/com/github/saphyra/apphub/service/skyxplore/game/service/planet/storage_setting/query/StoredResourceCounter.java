package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
class StoredResourceCounter {
    private final ResourceDataService resourceDataService;

    int countStoredResources(StorageType storageType, Map<String, StoredResource> storedResources) {
        return storedResources.values()
            .stream()
            .filter(storedResource -> resourceDataService.get(storedResource.getDataId()).getStorageType().equals(storageType))
            .mapToInt(StoredResource::getAmount)
            .sum();
    }
}
