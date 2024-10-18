package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.farm;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirementsValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class CultivationValidator {
    private final ConstructionRequirementsValidator constructionRequirementsValidator;
    private final ResourceDataService resourceDataService;

    void validate(List<Cultivation> produces) {
        ValidationUtil.notEmpty(produces, "produces");

        produces.forEach(this::validate);
    }

    private void validate(Cultivation cultivation) {
        constructionRequirementsValidator.validate(cultivation.getConstructionRequirements());
        ValidationUtil.atLeast(cultivation.getAmount(), 1, "amount");

        ValidationUtil.notBlank(cultivation.getResourceDataId(), "resourceDataId");
        ValidationUtil.valid(() -> resourceDataService.containsKey(cultivation.getResourceDataId()), "resourceDataId", "Resource not found by id " + cultivation.getResourceDataId());
    }
}
