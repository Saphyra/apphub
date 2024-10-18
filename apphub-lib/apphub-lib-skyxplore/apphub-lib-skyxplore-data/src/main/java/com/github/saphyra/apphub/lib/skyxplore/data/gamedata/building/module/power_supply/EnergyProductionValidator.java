package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.power_supply;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirementsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
//TODO unit test
class EnergyProductionValidator {
    private final ConstructionRequirementsValidator constructionRequirementsValidator;

    public void validate(List<EnergyProduction> productions) {
        ValidationUtil.notNull(productions, "productions");

        productions.forEach(this::validate);
    }

    private void validate(EnergyProduction energyProduction) {
        constructionRequirementsValidator.validate(energyProduction.getConstructionRequirements());

        ValidationUtil.notBlank(energyProduction.getId(), "id");
        ValidationUtil.atLeast(energyProduction.getEnergyPerBatch(), 0, "energyPerBatch");
        ValidationUtil.atLeast(energyProduction.getBatchTicks(), 0, "batchTicks");
        ValidationUtil.atLeast(energyProduction.getFuelLastsForTicks(), 0, "fuelLastsForTicks");
        ValidationUtil.notNull(energyProduction.getHumanPowered(), "humanPowered");
    }
}
