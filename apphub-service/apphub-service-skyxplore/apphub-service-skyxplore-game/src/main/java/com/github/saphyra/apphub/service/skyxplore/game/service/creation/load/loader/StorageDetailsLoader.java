package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class StorageDetailsLoader {
    private final AllocatedResourceLoader allocatedResourceLoader;
    private final ReservedStorageLoader reservedStorageLoader;
    private final StoredResourceLoader storedResourceLoader;
    private final StorageSettingLoader storageSettingLoader;

    StorageDetails load(UUID gameId, UUID location) {
        return StorageDetails.builder()
            .allocatedResources(allocatedResourceLoader.load(location))
            .reservedStorages(reservedStorageLoader.load(location))
            .storedResources(storedResourceLoader.load(gameId, location))
            .storageSettings(storageSettingLoader.load(location))
            .build();
    }
}
