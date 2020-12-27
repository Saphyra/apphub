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
//TODO unit test
public class ProductionValidator implements DataValidator<Map<String, Production>> {
    private final ConstructionRequirementsValidator constructionRequirementsValidator;

    @Override
    public void validate(Map<String, Production> item) {
        requireNonNull(item, "Gives must not be null.");
        if (isEmpty(item)) {
            throw new IllegalStateException("Production building must produce at least 1 resource");
        }
        item.forEach(this::validate);
    }

    private void validate(String resourceId, Production production) {
        try {
            requireNonNull(production, "Production must not be null.");
            requireNonNull(production.getPlaced(), "Placed must not be null");
            if (isEmpty(production.getPlaced())) {
                throw new IllegalStateException("Production has to be placed somewhere.");
            }

            if (production.getPlaced().stream().anyMatch(Objects::isNull)) {
                throw new NullPointerException("Placed contains null.");
            }

            requireNonNull(production.getAmount(), "Amount must not be null.");
            requireNonNull(production.getRequiredSkill(), "RequiredSkill must not be null.");

            requireNonNull(production.getConstructionRequirements(), "ConstructionRequirements must not be null.");
            constructionRequirementsValidator.validate(production.getConstructionRequirements());
        } catch (Exception e) {
            throw new IllegalStateException("Invalid Gives for resourceId " + resourceId, e);
        }
    }
}
