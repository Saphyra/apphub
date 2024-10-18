package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.cultural;

import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import org.springframework.stereotype.Component;

@Component
public class CulturalBuildingModuleService extends ValidationAbstractDataService<String, CulturalBuildingModule> {
    public CulturalBuildingModuleService(ContentLoaderFactory contentLoaderFactory, CulturalBuildingModuleValidator validator) {
        super("/data/building/module/cultural", contentLoaderFactory, validator);
    }

    @Override
    public void init() {
        load(CulturalBuildingModule.class);
    }

    @Override
    public void addItem(CulturalBuildingModule content, String fileName) {
        put(content.getId(), content);
    }
}
