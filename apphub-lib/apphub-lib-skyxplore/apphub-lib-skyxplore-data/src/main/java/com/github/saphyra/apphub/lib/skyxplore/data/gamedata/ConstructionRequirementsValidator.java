package com.github.saphyra.apphub.lib.skyxplore.data.gamedata;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConstructionRequirementsValidator implements DataValidator<ConstructionRequirements> {
    private final ResourceDataService resourceDataService;

    @Override
    public void validate(ConstructionRequirements item) {
        ValidationUtil.notNull(item, "constructionRequirements");

        ValidationUtil.atLeast(item.getRequiredWorkPoints(), 0, "requiredWorkPoints");
        ValidationUtil.atLeast(item.getRequiredEnergy(), 0, "requiredEnergy");
        ValidationUtil.doesNotContainNull(item.getRequiredResources(), "requiredResources");

        item.getRequiredResources()
            .keySet()
            .forEach(resourceDataId -> ValidationUtil.containsKey(resourceDataId, resourceDataService, "requiredResource=%s".formatted(resourceDataId)));
    }
}
