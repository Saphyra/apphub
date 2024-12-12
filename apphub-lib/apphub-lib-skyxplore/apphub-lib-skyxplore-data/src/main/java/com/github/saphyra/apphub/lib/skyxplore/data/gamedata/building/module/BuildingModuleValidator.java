package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirementsValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItemValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuildingModuleValidator {
    private final GameDataItemValidator gameDataItemValidator;
    private final ConstructionRequirementsValidator constructionRequirementsValidator;

    public void validate(BuildingModuleData buildingModuleData) {
        gameDataItemValidator.validate(buildingModuleData);
        constructionRequirementsValidator.validate(buildingModuleData.getConstructionRequirements());

        ValidationUtil.notNull(buildingModuleData.getCategory(), "category");
    }
}
