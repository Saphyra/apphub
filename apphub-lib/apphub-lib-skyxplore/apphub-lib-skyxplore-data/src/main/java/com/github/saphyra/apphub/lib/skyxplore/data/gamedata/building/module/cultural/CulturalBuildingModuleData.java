package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.cultural;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CulturalBuildingModuleData extends BuildingModuleData {
    private Integer capacity;
}
