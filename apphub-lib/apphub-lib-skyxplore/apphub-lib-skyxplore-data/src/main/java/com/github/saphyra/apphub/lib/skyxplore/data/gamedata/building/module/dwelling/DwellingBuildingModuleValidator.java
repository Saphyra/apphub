package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.dwelling;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
class DwellingBuildingModuleValidator implements DataValidator<Map<String, DwellingBuildingModuleData>> {
    private final BuildingModuleValidator buildingModuleValidator;

    @Override
    public void validate(Map<String, DwellingBuildingModuleData> item) {
        item.forEach(this::validate);
    }

    private void validate(String id, DwellingBuildingModuleData dwellingBuildingModule) {
        buildingModuleValidator.validate(dwellingBuildingModule);

        ValidationUtil.atLeast(dwellingBuildingModule.getCapacity(), 1, "capacity");
        ValidationUtil.atLeast(dwellingBuildingModule.getMoraleRecovery(), 0, "moraleRecovery");
    }
}
