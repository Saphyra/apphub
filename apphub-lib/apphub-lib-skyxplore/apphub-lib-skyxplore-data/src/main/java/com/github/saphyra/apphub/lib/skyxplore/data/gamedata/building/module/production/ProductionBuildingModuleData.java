package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.production;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProducerBuildingModule;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.Production;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProductionBuildingModuleData extends BuildingModuleData implements ProducerBuildingModule {
    private List<Production> produces;
}