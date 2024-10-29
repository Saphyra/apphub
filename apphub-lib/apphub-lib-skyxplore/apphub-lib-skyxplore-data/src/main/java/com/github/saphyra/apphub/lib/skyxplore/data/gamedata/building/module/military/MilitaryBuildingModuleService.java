package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.military;

import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;

//@Component
public class MilitaryBuildingModuleService extends ValidationAbstractDataService<String, MilitaryBuildingModuleData> {
    public MilitaryBuildingModuleService(ContentLoaderFactory contentLoaderFactory, MilitaryBuildingModuleValidator validator) {
        super("/data/building/module/military", contentLoaderFactory, validator);
    }

    @Override
    public void init() {
        load(MilitaryBuildingModuleData.class);
    }

    @Override
    public void addItem(MilitaryBuildingModuleData content, String fileName) {
        put(content.getId(), content);
    }
}
