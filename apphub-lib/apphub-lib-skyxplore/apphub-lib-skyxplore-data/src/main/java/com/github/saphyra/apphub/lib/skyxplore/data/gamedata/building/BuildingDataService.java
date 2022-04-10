package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building;

import com.github.saphyra.apphub.lib.data.AbstractDataService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class BuildingDataService extends HashMap<String, BuildingData> {
    public BuildingDataService(List<AbstractDataService<String, ? extends BuildingData>> services) {
        services.stream()
            .flatMap(stringAbstractDataService -> stringAbstractDataService.entrySet().stream())
            .forEach(stringEntry -> put(stringEntry.getKey(), stringEntry.getValue()));
    }
}
