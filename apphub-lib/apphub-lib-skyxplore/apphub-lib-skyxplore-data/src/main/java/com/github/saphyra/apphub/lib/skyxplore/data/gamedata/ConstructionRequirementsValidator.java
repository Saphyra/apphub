package com.github.saphyra.apphub.lib.skyxplore.data.gamedata;

import com.github.saphyra.apphub.lib.data.DataValidator;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@Component
public class ConstructionRequirementsValidator implements DataValidator<ConstructionRequirements> {
    @Override
    public void validate(ConstructionRequirements item) {
        requireNonNull(item.getParallelWorkers(), "parallelWorkers must not be null.");
        requireNonNull(item.getRequiredWorkPoints(), "requiredWorkPoints must not be null.");
        if (item.getRequiredWorkPoints() < 1) {
            throw new IllegalStateException("requiredWorkPoints must be higher than 0");
        }

        requireNonNull(item.getRequiredResources(), "RequiredResources must not be null.");
        if (item.getRequiredResources().entrySet().stream().anyMatch(e -> isNull(e.getValue()))) {
            throw new NullPointerException("RequiredResources must not contain null.");
        }
    }
}
