package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module;

import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import com.github.saphyra.apphub.lib.data.AbstractDataService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class BuildingModuleDataService extends HashMap<String, BuildingModuleData> implements OptionalMap<String, BuildingModuleData> {
    BuildingModuleDataService(List<AbstractDataService<String, ? extends BuildingModuleData>> services) {
        services.forEach(this::putAll);
    }
}
