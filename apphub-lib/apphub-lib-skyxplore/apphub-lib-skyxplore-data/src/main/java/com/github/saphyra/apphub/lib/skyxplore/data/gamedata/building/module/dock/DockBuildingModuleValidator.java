package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.dock;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
class DockBuildingModuleValidator implements DataValidator<Map<String, DockBuildingModuleData>> {
    private final BuildingModuleValidator buildingModuleValidator;

    @Override
    public void validate(Map<String, DockBuildingModuleData> item) {
        item.forEach(this::validate);
    }

    private void validate(String id, DockBuildingModuleData dockBuildingModule) {
        buildingModuleValidator.validate(dockBuildingModule);

        ValidationUtil.notNull(dockBuildingModule.getSize(), "size");
    }
}
