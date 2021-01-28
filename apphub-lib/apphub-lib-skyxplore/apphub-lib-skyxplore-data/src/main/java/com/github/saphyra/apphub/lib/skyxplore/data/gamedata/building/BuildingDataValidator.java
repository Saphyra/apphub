package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building;

import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirementsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.ObjectUtils.isEmpty;

@Component
@RequiredArgsConstructor
public class BuildingDataValidator implements DataValidator<BuildingData> {
    private final ConstructionRequirementsValidator constructionRequirementsValidator;
    private final GameDataItemValidator gameDataItemValidator;

    @Override
    public void validate(BuildingData buildingData) {
        gameDataItemValidator.validate(buildingData);
        requireNonNull(buildingData.getBuildingType(), "BuildingType must not be null.");
        if (isEmpty(buildingData.getConstructionRequirements())) {
            throw new IllegalStateException("ConstructionRequirements must be filled.");
        }
        buildingData.getConstructionRequirements().values().forEach(constructionRequirementsValidator::validate);
    }
}
