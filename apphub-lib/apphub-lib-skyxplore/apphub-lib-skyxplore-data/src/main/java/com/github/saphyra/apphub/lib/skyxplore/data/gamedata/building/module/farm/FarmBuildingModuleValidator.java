package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.farm;

import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
class FarmBuildingModuleValidator implements DataValidator<Map<String, FarmBuildingModule>> {
    private final BuildingModuleValidator buildingModuleValidator;
    private final CultivationValidator cultivationValidator;

    @Override
    public void validate(Map<String, FarmBuildingModule> item) {
        item.forEach(this::validate);
    }

    private void validate(String id, FarmBuildingModule farmBuildingModule) {
        buildingModuleValidator.validate(farmBuildingModule);
        cultivationValidator.validate(farmBuildingModule.getProduces());
    }
}
