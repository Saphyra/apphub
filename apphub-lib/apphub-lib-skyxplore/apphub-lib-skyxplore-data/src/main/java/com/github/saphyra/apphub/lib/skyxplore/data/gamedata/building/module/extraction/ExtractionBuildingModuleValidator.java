package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.extraction;

import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
class ExtractionBuildingModuleValidator implements DataValidator<Map<String, ExtractionBuildingModuleData>> {
    private final BuildingModuleValidator buildingModuleValidator;
    private final ProductionValidator productionValidator;

    @Override
    public void validate(Map<String, ExtractionBuildingModuleData> item) {
        item.forEach(this::validate);
    }

    private void validate(String id, ExtractionBuildingModuleData extractionBuildingModule) {
        buildingModuleValidator.validate(extractionBuildingModule);
        productionValidator.validate(extractionBuildingModule.getProduces());
    }
}
