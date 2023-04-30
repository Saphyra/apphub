package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage;

import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingDataValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageBuildingValidator implements DataValidator<Map<String, StorageBuildingData>> {
    private final BuildingDataValidator buildingDataValidator;

    @Override
    public void validate(Map<String, StorageBuildingData> item) {
        item.forEach(this::validate);
    }

    private void validate(String key, StorageBuildingData storageBuildingData) {
        try {
            log.debug("Validating StorageBuilding with key {}", key);
            buildingDataValidator.validate(storageBuildingData);
            requireNonNull(storageBuildingData.getStores(), "Stores must not be null.");
            requireNonNull(storageBuildingData.getCapacity(), "Capacity must not be null.");
            if (storageBuildingData.getCapacity() < 1) {
                throw new IllegalStateException("Capacity is " + storageBuildingData.getCapacity() + " what is lower than 1");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Invalid data with key " + key, e);
        }
    }
}
