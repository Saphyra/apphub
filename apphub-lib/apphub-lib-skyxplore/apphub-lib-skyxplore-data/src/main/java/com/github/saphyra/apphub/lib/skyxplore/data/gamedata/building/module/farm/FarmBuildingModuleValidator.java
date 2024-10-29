package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.farm;

import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
class FarmBuildingModuleValidator implements DataValidator<Map<String, FarmBuildingModuleData>> {
    private final BuildingModuleValidator buildingModuleValidator;
    private final ProductionValidator productionValidator;

    @Override
    public void validate(Map<String, FarmBuildingModuleData> item) {
        item.forEach(this::validate);
    }

    private void validate(String id, FarmBuildingModuleData farmBuildingModule) {
        buildingModuleValidator.validate(farmBuildingModule);
        productionValidator.validate(farmBuildingModule.getProduces());
    }
}
