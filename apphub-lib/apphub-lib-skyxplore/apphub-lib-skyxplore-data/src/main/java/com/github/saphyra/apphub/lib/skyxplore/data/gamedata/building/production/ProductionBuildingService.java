package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production;

import com.github.saphyra.apphub.lib.data.AbstractDataService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class ProductionBuildingService extends HashMap<String, ProducerBuildingModule> {
    ProductionBuildingService(List<AbstractDataService<String, ? extends ProducerBuildingModule>> services) {
        services.forEach(this::putAll);
    }
}
