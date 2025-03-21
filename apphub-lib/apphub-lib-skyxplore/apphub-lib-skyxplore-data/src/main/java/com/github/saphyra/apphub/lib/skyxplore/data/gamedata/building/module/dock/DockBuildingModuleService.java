package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.dock;

import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import org.springframework.stereotype.Component;

@Component
public class DockBuildingModuleService extends ValidationAbstractDataService<String, DockBuildingModuleData> {
    DockBuildingModuleService(ContentLoaderFactory contentLoaderFactory, DockBuildingModuleValidator validator) {
        super("/data/building/module/dock", contentLoaderFactory, validator);
    }

    @Override
    public void init() {
        load(DockBuildingModuleData.class);
    }

    @Override
    public void addItem(DockBuildingModuleData content, String fileName) {
        put(content.getId(), content);
    }
}
