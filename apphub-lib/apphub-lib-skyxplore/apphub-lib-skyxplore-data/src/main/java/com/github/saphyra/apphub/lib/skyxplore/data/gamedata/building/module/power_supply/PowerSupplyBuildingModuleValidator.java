package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.power_supply;

import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
//TODO unit test
public class PowerSupplyBuildingModuleValidator implements DataValidator<Map<String, PowerSupplyBuildingModuleData>> {
    private final BuildingModuleValidator buildingModuleValidator;
    private final EnergyProductionValidator energyProductionValidator;

    @Override
    public void validate(Map<String, PowerSupplyBuildingModuleData> item) {
        item.forEach(this::validate);
    }

    private void validate(String id, PowerSupplyBuildingModuleData powerSupplyBuildingModule) {
        buildingModuleValidator.validate(powerSupplyBuildingModule);
        energyProductionValidator.validate(powerSupplyBuildingModule.getProductions());
    }
}
