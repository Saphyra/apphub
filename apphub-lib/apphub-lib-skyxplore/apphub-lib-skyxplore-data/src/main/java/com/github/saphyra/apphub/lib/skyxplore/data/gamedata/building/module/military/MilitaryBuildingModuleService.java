package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.military;

import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import org.springframework.stereotype.Component;

@Component
public class MilitaryBuildingModuleService extends ValidationAbstractDataService<String, MilitaryBuildingModule> {
    public MilitaryBuildingModuleService(ContentLoaderFactory contentLoaderFactory, MilitaryBuildingModuleValidator validator) {
        super("/data/building/module/military", contentLoaderFactory, validator);
    }

    @Override
    public void init() {
        load(MilitaryBuildingModule.class);
    }

    @Override
    public void addItem(MilitaryBuildingModule content, String fileName) {
        put(content.getId(), content);
    }
}
