package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirementsValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
//TODO unit test
public class ProductionValidator {
    private final ConstructionRequirementsValidator constructionRequirementsValidator;
    private final ResourceDataService resourceDataService;

    public void validate(List<Production> produces) {
        ValidationUtil.notEmpty(produces, "produces");

        produces.forEach(this::validate);
    }

    private void validate(Production production) {
        constructionRequirementsValidator.validate(production.getConstructionRequirements());
        ValidationUtil.atLeast(production.getAmount(), 1, "amount");

        ValidationUtil.notBlank(production.getResourceDataId(), "resourceDataId");
        ValidationUtil.valid(() -> resourceDataService.containsKey(production.getResourceDataId()), "resourceDataId", "Resource not found by id " + production.getResourceDataId());
        ValidationUtil.notNull(production.getRequiredSkill(), "requiredSkill");
    }
}
