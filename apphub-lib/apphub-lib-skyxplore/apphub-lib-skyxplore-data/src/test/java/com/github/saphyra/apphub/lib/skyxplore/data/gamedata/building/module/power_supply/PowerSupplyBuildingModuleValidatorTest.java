package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.power_supply;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PowerSupplyBuildingModuleValidatorTest {
    @Mock
    private BuildingModuleValidator buildingModuleValidator;

    @Mock
    private EnergyProductionValidator energyProductionValidator;

    @InjectMocks
    private PowerSupplyBuildingModuleValidator underTest;

    @Mock
    private PowerSupplyBuildingModuleData data;

    @Mock
    private EnergyProduction energyProduction;

    @Test
    void validate() {
        given(data.getProductions()).willReturn(List.of(energyProduction));

        underTest.validate(Map.of("asd", data));

        then(buildingModuleValidator).should().validate(data);
        then(energyProductionValidator).should().validate(List.of(energyProduction));
    }
}