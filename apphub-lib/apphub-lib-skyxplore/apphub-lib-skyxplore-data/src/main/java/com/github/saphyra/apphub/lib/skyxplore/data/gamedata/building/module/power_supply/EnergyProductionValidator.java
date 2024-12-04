package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.power_supply;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
//TODO unit test
class EnergyProductionValidator {
    public void validate(List<EnergyProduction> productions) {
        ValidationUtil.notNull(productions, "productions");

        productions.forEach(this::validate);
    }

    private void validate(EnergyProduction energyProduction) {
        ValidationUtil.notBlank(energyProduction.getId(), "id");
        ValidationUtil.atLeast(energyProduction.getEnergyPerBatch(), 0, "energyPerBatch");
        ValidationUtil.atLeast(energyProduction.getBatchTicks(), 0, "batchTicks");
        ValidationUtil.atLeast(energyProduction.getFuelLastsForTicks(), 0, "fuelLastsForTicks");
        ValidationUtil.notNull(energyProduction.getFuelStorage(), "fuelStorage");
        ValidationUtil.notNull(energyProduction.getHumanPowered(), "humanPowered");

        //TODO check if resource exists if fuel is set
    }
}
