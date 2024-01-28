package com.github.saphyra.apphub.service.skyxplore.game.util;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.miscellaneous.MiscellaneousBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.miscellaneous.MiscellaneousBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeadquartersUtil {
    private final MiscellaneousBuildingService miscellaneousBuildingService;
    private final ObjectMapperWrapper objectMapperWrapper;

    public Collection<String> getGives() {
        MiscellaneousBuilding data = getMiscellaneousBuilding();

        return objectMapperWrapper.convertValue(data.getData().get("gives"), GivesMap.class)
            .keySet();
    }

    public ProductionData getProductionData(String dataId) {
        MiscellaneousBuilding data = getMiscellaneousBuilding();

        return objectMapperWrapper.convertValue(data.getData().get("gives"), GivesMap.class)
            .get(dataId);
    }

    public Integer getWorkers() {
        Object workers = getMiscellaneousBuilding()
            .getData()
            .get("workers");
        return objectMapperWrapper.convertValue(workers, Integer.class);
    }

    public Map<StorageType, Integer> getStores() {
        Object stores = getMiscellaneousBuilding()
            .getData()
            .get("stores");
        return objectMapperWrapper.convertValue(stores, StoresMap.class);
    }

    private MiscellaneousBuilding getMiscellaneousBuilding() {
        return miscellaneousBuildingService.get(GameConstants.DATA_ID_HEADQUARTERS);
    }

    private static class GivesMap extends HashMap<String, ProductionData> {
    }

    private static class StoresMap extends HashMap<StorageType, Integer> {
    }
}
