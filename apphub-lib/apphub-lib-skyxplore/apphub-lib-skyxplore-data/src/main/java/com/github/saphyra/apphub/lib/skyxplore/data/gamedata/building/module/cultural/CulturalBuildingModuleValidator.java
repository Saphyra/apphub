package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.cultural;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
class CulturalBuildingModuleValidator implements DataValidator<Map<String, CulturalBuildingModuleData>> {
    private final BuildingModuleValidator buildingModuleValidator;

    @Override
    public void validate(Map<String, CulturalBuildingModuleData> item) {
        item.forEach(this::validate);
    }

    private void validate(String s, CulturalBuildingModuleData culturalBuildingModule) {
        buildingModuleValidator.validate(culturalBuildingModule);

        ValidationUtil.atLeast(culturalBuildingModule.getCapacity(), 1, "capacity");
    }
}
