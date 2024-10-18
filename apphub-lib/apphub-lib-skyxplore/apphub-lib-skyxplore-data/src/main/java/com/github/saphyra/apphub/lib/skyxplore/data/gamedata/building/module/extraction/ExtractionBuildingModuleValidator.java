package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.extraction;

import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
class ExtractionBuildingModuleValidator implements DataValidator<Map<String, ExtractionBuildingModule>> {
    private final BuildingModuleValidator buildingModuleValidator;
    private final ExtractionValidator extractionValidator;

    @Override
    public void validate(Map<String, ExtractionBuildingModule> item) {
        item.forEach(this::validate);
    }

    private void validate(String id, ExtractionBuildingModule extractionBuildingModule) {
        buildingModuleValidator.validate(extractionBuildingModule);
        extractionValidator.validate(extractionBuildingModule.getProduces());
    }
}
