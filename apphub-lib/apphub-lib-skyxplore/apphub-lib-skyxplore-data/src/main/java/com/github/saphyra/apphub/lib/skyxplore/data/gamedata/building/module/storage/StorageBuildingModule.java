package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.storage;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModule;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StorageBuildingModule extends BuildingModule {
    private Map<StorageType, Integer> stores;
}
