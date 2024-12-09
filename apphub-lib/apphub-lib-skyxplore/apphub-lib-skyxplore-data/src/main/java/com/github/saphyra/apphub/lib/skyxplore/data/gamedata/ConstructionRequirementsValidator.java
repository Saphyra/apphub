package com.github.saphyra.apphub.lib.skyxplore.data.gamedata;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.stereotype.Component;

@Component
//TODO unit test
public class ConstructionRequirementsValidator implements DataValidator<ConstructionRequirements> {
    @Override
    public void validate(ConstructionRequirements item) {
        ValidationUtil.notNull(item, "constructionRequirements");

        ValidationUtil.atLeast(item.getRequiredWorkPoints(), 0, "requiredWorkPoints");
        ValidationUtil.atLeast(item.getRequiredEnergy(), 0, "requiredEnergy");
        ValidationUtil.doesNotContainNull(item.getRequiredResources(), "requiredResources");

        if (item.getRequiredResources().containsKey("energy")) {
            throw ExceptionFactory.invalidParam("energy", "must not be present");
        }
    }
}
