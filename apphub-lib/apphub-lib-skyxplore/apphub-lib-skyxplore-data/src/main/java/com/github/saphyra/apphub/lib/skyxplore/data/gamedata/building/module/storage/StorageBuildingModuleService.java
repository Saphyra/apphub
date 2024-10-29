package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.storage;

import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import org.springframework.stereotype.Component;

@Component
public class StorageBuildingModuleService extends ValidationAbstractDataService<String, StorageBuildingModuleData> {
    public StorageBuildingModuleService(ContentLoaderFactory contentLoaderFactory, StorageBuildingModuleValidator validator) {
        super("/data/building/module/storage", contentLoaderFactory, validator);
    }

    @Override
    public void init() {
        load(StorageBuildingModuleData.class);
    }

    @Override
    public void addItem(StorageBuildingModuleData content, String fileName) {
        put(content.getId(), content);
    }
}
