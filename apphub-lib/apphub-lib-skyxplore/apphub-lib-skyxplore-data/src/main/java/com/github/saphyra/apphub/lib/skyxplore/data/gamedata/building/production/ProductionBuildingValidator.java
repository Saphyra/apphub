package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production;

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
public class ProductionBuildingValidator implements DataValidator<Map<String, ProductionBuildingData>> {
    private final BuildingDataValidator buildingDataValidator;
    private final ProductionDataValidator productionDataValidator;

    @Override
    public void validate(Map<String, ProductionBuildingData> item) {
        item.forEach(this::validate);
    }

    private void validate(String key, ProductionBuildingData productionBuildingData) {
        try {
            log.debug("Validating ProductionBuilding with key {}", key);
            buildingDataValidator.validate(productionBuildingData);
            requireNonNull(productionBuildingData.getWorkers(), "Workers must not be null.");
            if (productionBuildingData.getWorkers() < 1) {
                throw new IllegalStateException("Workers must be higher than 0");
            }
            requireNonNull(productionBuildingData.getPrimarySurfaceType(), "PrimarySurfaceType must not be null.");
            requireNonNull(productionBuildingData.getPlaceableSurfaceTypes(), "PlaceableSurfaceTypes must not be null.");
            productionDataValidator.validate(productionBuildingData.getGives());
        } catch (Exception e) {
            throw new IllegalStateException("Invalid data with key " + key, e);
        }
    }
}
