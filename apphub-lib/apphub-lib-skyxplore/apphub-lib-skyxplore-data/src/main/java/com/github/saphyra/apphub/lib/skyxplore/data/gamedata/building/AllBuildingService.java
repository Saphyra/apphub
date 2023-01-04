package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building;

import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import com.github.saphyra.apphub.lib.data.AbstractDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AllBuildingService extends HashMap<String, BuildingData> implements OptionalMap<String, BuildingData> {
    public AllBuildingService(List<AbstractDataService<String, ? extends BuildingData>> buildingDataServices) {
        buildingDataServices.forEach(this::putAll);
    }

    @PostConstruct
    public void log() {
        log.debug("All buildings collected: {}", values().stream().map(GameDataItem::getId).collect(Collectors.joining(", ")));
    }
}
