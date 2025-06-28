package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.dwelling;

import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import org.springframework.stereotype.Component;

@Component
public class DwellingBuildingDataService extends ValidationAbstractDataService<String, DwellingBuildingModuleData> {
    public DwellingBuildingDataService(ContentLoaderFactory contentLoaderFactory, DwellingBuildingModuleValidator validator) {
        super("/data/building/module/dwelling", contentLoaderFactory, validator);
    }

    @Override
    public void init() {
        load(DwellingBuildingModuleData.class);
    }

    @Override
    public void addItem(DwellingBuildingModuleData content, String fileName) {
        put(content.getId(), content);
    }
}
