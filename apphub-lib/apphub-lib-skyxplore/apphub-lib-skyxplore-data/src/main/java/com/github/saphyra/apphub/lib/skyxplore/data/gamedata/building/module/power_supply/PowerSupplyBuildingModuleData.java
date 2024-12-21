package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.power_supply;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PowerSupplyBuildingModuleData extends BuildingModuleData {
    private List<EnergyProduction> productions;
}
