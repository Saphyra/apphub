package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.storage;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class StorageBuildingModuleValidator implements DataValidator<Map<String, StorageBuildingModuleData>> {
    private final BuildingModuleValidator buildingModuleValidator;

    public void validate(Map<String, StorageBuildingModuleData> item) {
        item.forEach(this::validate);
    }

    private void validate(String id, StorageBuildingModuleData storageBuildingModule) {
        buildingModuleValidator.validate(storageBuildingModule);

        ValidationUtil.notEmpty(storageBuildingModule.getStores(), "stores");
        ValidationUtil.doesNotContainNull(storageBuildingModule.getStores(), "stores");
    }
}
