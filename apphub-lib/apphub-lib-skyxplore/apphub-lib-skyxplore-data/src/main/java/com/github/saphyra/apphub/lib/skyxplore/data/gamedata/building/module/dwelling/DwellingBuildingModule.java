package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.dwelling;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModule;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DwellingBuildingModule extends BuildingModule {
    private Integer capacity;
    private Integer moraleRecovery;
}
