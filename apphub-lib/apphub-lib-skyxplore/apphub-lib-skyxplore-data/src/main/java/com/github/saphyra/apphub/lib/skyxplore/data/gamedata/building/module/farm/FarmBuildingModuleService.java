package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.farm;

import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import org.springframework.stereotype.Component;

@Component
public class FarmBuildingModuleService extends ValidationAbstractDataService<String, FarmBuildingModule> {
    public FarmBuildingModuleService(ContentLoaderFactory contentLoaderFactory, FarmBuildingModuleValidator validator) {
        super("/data/building/module/farm", contentLoaderFactory, validator);
    }

    @Override
    public void init() {
        load(FarmBuildingModule.class);
    }

    @Override
    public void addItem(FarmBuildingModule content, String fileName) {
        put(content.getId(), content);
    }
}
