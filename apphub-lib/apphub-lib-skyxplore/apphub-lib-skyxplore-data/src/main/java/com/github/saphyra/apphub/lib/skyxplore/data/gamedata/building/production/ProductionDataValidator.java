package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production;

import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirementsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.ObjectUtils.isEmpty;

@Component
@RequiredArgsConstructor
public class ProductionDataValidator implements DataValidator<Map<String, ProductionData>> {
    private final ConstructionRequirementsValidator constructionRequirementsValidator;

    @Override
    public void validate(Map<String, ProductionData> item) {
        requireNonNull(item, "Gives must not be null.");
        if (isEmpty(item)) {
            throw new IllegalStateException("Production building must produce at least 1 resource");
        }
        item.forEach(this::validate);
    }

    private void validate(String resourceId, ProductionData productionData) {
        try {
            requireNonNull(productionData, "Production must not be null.");
            requireNonNull(productionData.getMaxBatchSize(), "MaxBatchSize must not be null.");
            requireNonNull(productionData.getPlaced(), "Placed must not be null");
            if (isEmpty(productionData.getPlaced())) {
                throw new IllegalStateException("Production has to be placed somewhere.");
            }

            if (productionData.getPlaced().stream().anyMatch(Objects::isNull)) {
                throw new NullPointerException("Placed contains null.");
            }

            requireNonNull(productionData.getRequiredSkill(), "RequiredSkill must not be null.");

            requireNonNull(productionData.getConstructionRequirements(), "ConstructionRequirements must not be null.");
            constructionRequirementsValidator.validate(productionData.getConstructionRequirements());
        } catch (Exception e) {
            throw new IllegalStateException("Invalid Gives for resourceId " + resourceId, e);
        }
    }
}
