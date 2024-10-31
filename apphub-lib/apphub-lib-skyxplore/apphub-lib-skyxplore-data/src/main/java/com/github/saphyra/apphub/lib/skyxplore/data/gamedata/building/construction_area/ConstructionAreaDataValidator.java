package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.construction_area;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirementsValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItemValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
//TODO unit test
public class ConstructionAreaDataValidator implements DataValidator<Map<String, ConstructionAreaData>> {
    private final GameDataItemValidator gameDataItemValidator;
    private final ConstructionRequirementsValidator constructionRequirementsValidator;

    @Override
    public void validate(Map<String, ConstructionAreaData> item) {
        item.forEach(this::validate);
    }

    private void validate(String id, ConstructionAreaData constructionAreaData) {
        gameDataItemValidator.validate(constructionAreaData);
        constructionRequirementsValidator.validate(constructionAreaData.getConstructionRequirements());

        ValidationUtil.notEmpty(constructionAreaData.getSupportedSurfaces(), "supportedSurfaces");
        ValidationUtil.notEmpty(constructionAreaData.getSlots(), "slots");
    }
}
