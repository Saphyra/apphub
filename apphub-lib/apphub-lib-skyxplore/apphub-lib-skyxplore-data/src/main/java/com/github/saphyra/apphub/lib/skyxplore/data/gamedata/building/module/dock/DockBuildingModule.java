package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.dock;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ShipSize;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModule;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DockBuildingModule extends BuildingModule {
    private ShipSize size;
}
