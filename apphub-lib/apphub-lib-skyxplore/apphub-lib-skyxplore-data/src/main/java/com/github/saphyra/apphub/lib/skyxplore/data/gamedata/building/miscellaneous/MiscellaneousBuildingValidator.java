package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.miscellaneous;

import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingDataValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@Component
@Slf4j
@RequiredArgsConstructor
public class MiscellaneousBuildingValidator implements DataValidator<Map<String, MiscellaneousBuilding>> {
    private final BuildingDataValidator buildingDataValidator;

    @Override
    public void validate(Map<String, MiscellaneousBuilding> item) {
        item.forEach(this::validate);
    }

    private void validate(String key, MiscellaneousBuilding miscellaneousBuilding) {
        try {
            log.debug("Validating MiscellaneousBuilding with key {}", key);
            buildingDataValidator.validate(miscellaneousBuilding);
            requireNonNull(miscellaneousBuilding.getPlaceableSurfaceTypes(), "PlaceableSurfaceTypes must not be null");
            if (miscellaneousBuilding.getPlaceableSurfaceTypes().stream().anyMatch(Objects::isNull)) {
                throw new NullPointerException("PlaceableSurfaceTypes must not contain null.");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Invalid data with key " + key, e);
        }
    }
}
