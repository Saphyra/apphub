package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production;

import com.github.saphyra.apphub.lib.data.AbstractDataService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class ProductionBuildingModuleService extends HashMap<String, ProducerBuildingModule> {
    ProductionBuildingModuleService(List<AbstractDataService<String, ? extends ProducerBuildingModule>> services) {
        services.forEach(this::putAll);
    }
}
