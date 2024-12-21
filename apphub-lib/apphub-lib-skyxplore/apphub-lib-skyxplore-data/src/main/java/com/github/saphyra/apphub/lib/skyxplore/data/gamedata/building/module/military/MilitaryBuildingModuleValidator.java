package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.military;

import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
class MilitaryBuildingModuleValidator implements DataValidator<Map<String, MilitaryBuildingModuleData>> {
    private final BuildingModuleValidator buildingModuleValidator;

    @Override
    public void validate(Map<String, MilitaryBuildingModuleData> item) {
        item.forEach(this::validate);
    }

    private void validate(String id, MilitaryBuildingModuleData militaryBuildingModule) {
        buildingModuleValidator.validate(militaryBuildingModule);
    }
}
