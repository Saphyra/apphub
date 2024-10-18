package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirementsValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItemValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
//TODO unit test
public class BuildingModuleValidator {
    private final GameDataItemValidator gameDataItemValidator;
    private final ConstructionRequirementsValidator constructionRequirementsValidator;

    public void validate(BuildingModule buildingModule) {
        gameDataItemValidator.validate(buildingModule);
        constructionRequirementsValidator.validate(buildingModule.getConstructionRequirements());

        ValidationUtil.notNull(buildingModule.getCategory(), "category");
        ValidationUtil.notNull(buildingModule.getConstructionRequirements(), "constructionRequirements");
    }
}
