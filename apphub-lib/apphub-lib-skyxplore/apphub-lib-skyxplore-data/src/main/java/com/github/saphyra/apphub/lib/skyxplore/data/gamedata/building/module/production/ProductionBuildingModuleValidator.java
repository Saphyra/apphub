package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.production;

import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
class ProductionBuildingModuleValidator implements DataValidator<Map<String, ProductionBuildingModule>> {
    private final BuildingModuleValidator buildingModuleValidator;
    private final ProductionValidator productionValidator;

    @Override
    public void validate(Map<String, ProductionBuildingModule> item) {
        item.forEach(this::validate);
    }

    private void validate(String id, ProductionBuildingModule extractionBuildingModule) {
        buildingModuleValidator.validate(extractionBuildingModule);
        productionValidator.validate(extractionBuildingModule.getProduces());
    }
}
