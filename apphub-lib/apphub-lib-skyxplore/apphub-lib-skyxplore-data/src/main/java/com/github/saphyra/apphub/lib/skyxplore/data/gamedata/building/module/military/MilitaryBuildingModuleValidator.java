package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.military;

import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
//TODO unit test
class MilitaryBuildingModuleValidator implements DataValidator<Map<String, MilitaryBuildingModule>> {
    private final BuildingModuleValidator buildingModuleValidator;

    @Override
    public void validate(Map<String, MilitaryBuildingModule> item) {
        item.forEach(this::validate);
    }

    private void validate(String id, MilitaryBuildingModule militaryBuildingModule) {
        buildingModuleValidator.validate(militaryBuildingModule);
    }
}
