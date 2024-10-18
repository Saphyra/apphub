package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.production;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirementsValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class ProductionValidator {
    private final ConstructionRequirementsValidator constructionRequirementsValidator;
    private final ResourceDataService resourceDataService;

    void validate(List<Production> produces) {
        ValidationUtil.notEmpty(produces, "produces");

        produces.forEach(this::validate);
    }

    private void validate(Production extraction) {
        constructionRequirementsValidator.validate(extraction.getConstructionRequirements());
        ValidationUtil.atLeast(extraction.getAmount(), 1, "amount");

        ValidationUtil.notBlank(extraction.getResourceDataId(), "resourceDataId");
        ValidationUtil.valid(() -> resourceDataService.containsKey(extraction.getResourceDataId()), "resourceDataId", "Resource not found by id " + extraction.getResourceDataId());
    }
}
