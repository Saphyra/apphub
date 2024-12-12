package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.power_supply;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
class EnergyProductionValidator {
    private final ResourceDataService resourceDataService;

    public void validate(List<EnergyProduction> productions) {
        ValidationUtil.notNull(productions, "productions");

        productions.forEach(this::validate);
    }

    private void validate(EnergyProduction energyProduction) {
        ValidationUtil.notBlank(energyProduction.getId(), "id");
        ValidationUtil.atLeast(energyProduction.getEnergyPerBatch(), 0, "energyPerBatch");
        ValidationUtil.atLeast(energyProduction.getBatchTicks(), 0, "batchTicks");
        ValidationUtil.atLeast(energyProduction.getFuelLastsForTicks(), 0, "fuelLastsForTicks");
        ValidationUtil.atLeast(energyProduction.getFuelStorage(), 0,"fuelStorage");
        ValidationUtil.notNull(energyProduction.getHumanPowered(), "humanPowered");

        if (nonNull(energyProduction.getFuel())) {
            ValidationUtil.containsKey(energyProduction.getFuel(), resourceDataService, "fuel=%s".formatted(energyProduction.getFuel()));
        }
    }
}
