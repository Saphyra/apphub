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
public class ConstructionAreaValidator implements DataValidator<Map<String, ConstructionArea>> {
    private final GameDataItemValidator gameDataItemValidator;
    private final ConstructionRequirementsValidator constructionRequirementsValidator;

    @Override
    public void validate(Map<String, ConstructionArea> item) {
        item.forEach(this::validate);
    }

    private void validate(String id, ConstructionArea constructionArea) {
        gameDataItemValidator.validate(constructionArea);
        constructionRequirementsValidator.validate(constructionArea.getConstructionRequirements());

        ValidationUtil.notEmpty(constructionArea.getSupportedSurfaces(), "supportedSurfaces");
        ValidationUtil.notEmpty(constructionArea.getSlots(), "slots");
    }
}
