package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.cultural;

import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import org.springframework.stereotype.Component;

@Component
public class CulturalBuildingModuleService extends ValidationAbstractDataService<String, CulturalBuildingModuleData> {
    public CulturalBuildingModuleService(ContentLoaderFactory contentLoaderFactory, CulturalBuildingModuleValidator validator) {
        super("/data/building/module/cultural", contentLoaderFactory, validator);
    }

    @Override
    public void init() {
        load(CulturalBuildingModuleData.class);
    }

    @Override
    public void addItem(CulturalBuildingModuleData content, String fileName) {
        put(content.getId(), content);
    }
}
