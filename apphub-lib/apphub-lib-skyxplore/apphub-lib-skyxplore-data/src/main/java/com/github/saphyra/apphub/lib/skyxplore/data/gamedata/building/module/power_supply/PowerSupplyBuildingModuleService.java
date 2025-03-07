package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.power_supply;

import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import org.springframework.stereotype.Component;

@Component
public class PowerSupplyBuildingModuleService extends ValidationAbstractDataService<String, PowerSupplyBuildingModuleData> {
    public PowerSupplyBuildingModuleService(ContentLoaderFactory contentLoaderFactory, PowerSupplyBuildingModuleValidator validator) {
        super("/data/building/module/power_supply", contentLoaderFactory, validator);
    }

    @Override
    public void init() {
        load(PowerSupplyBuildingModuleData.class);
    }

    @Override
    public void addItem(PowerSupplyBuildingModuleData content, String fileName) {
        put(content.getId(), content);
    }
}
