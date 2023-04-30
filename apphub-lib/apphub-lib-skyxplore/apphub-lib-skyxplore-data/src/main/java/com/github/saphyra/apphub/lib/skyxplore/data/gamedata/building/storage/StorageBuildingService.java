package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage;

import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.util.Map;

@Component
@Slf4j
public class StorageBuildingService extends ValidationAbstractDataService<String, StorageBuildingData> {
    public StorageBuildingService(ContentLoaderFactory contentLoaderFactory, DataValidator<Map<String, StorageBuildingData>> dataValidator) {
        super("/data/building/storage", contentLoaderFactory, dataValidator);
    }

    @Override
    @PostConstruct
    public void init() {
        super.load(StorageBuildingData.class);
        log.debug("StorageBuildingService: {}", this);
    }

    @Override
    public void addItem(StorageBuildingData content, String fileName) {
        put(content.getId(), content);
    }

    public StorageBuildingData findByStorageType(StorageType storageType) {
        return values().stream()
            .filter(storageBuilding -> storageBuilding.getStores().equals(storageType))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(String.format("No storage found for storageType %s.", storageType)));
    }
}
