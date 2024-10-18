package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.dwelling;

import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import org.springframework.stereotype.Component;

@Component
public class DwellingBuildingService extends ValidationAbstractDataService<String, DwellingBuildingModule> {
    public DwellingBuildingService(ContentLoaderFactory contentLoaderFactory, DwellingBuildingModuleValidator validator) {
        super("/data/building/module/dwelling", contentLoaderFactory, validator);
    }

    @Override
    public void init() {
        load(DwellingBuildingModule.class);
    }

    @Override
    public void addItem(DwellingBuildingModule content, String fileName) {
        put(content.getId(), content);
    }
}
