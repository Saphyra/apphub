package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module;

import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import com.github.saphyra.apphub.lib.data.AbstractDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirementsProvider;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BuildingModuleDataService extends HashMap<String, BuildingModuleData> implements OptionalMap<String, BuildingModuleData>, ConstructionRequirementsProvider {
    BuildingModuleDataService(List<AbstractDataService<String, ? extends BuildingModuleData>> services) {
        services.forEach(this::putAll);
    }

    @Override
    public List<ConstructionRequirements> getConstructionRequirements() {
        return values()
            .stream()
            .map(BuildingModuleData::getConstructionRequirements)
            .collect(Collectors.toList());
    }
}

