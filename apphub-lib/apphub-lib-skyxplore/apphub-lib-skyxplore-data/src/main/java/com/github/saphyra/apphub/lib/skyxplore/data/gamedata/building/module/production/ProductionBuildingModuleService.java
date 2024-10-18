package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.production;

import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import org.springframework.stereotype.Component;

@Component
public class ProductionBuildingModuleService extends ValidationAbstractDataService<String, ProductionBuildingModule> {
    public ProductionBuildingModuleService(ContentLoaderFactory contentLoaderFactory, ProductionBuildingModuleValidator validator) {
        super("/data/building/module/production", contentLoaderFactory, validator);
    }

    @Override
    public void init() {
        load(ProductionBuildingModule.class);
    }

    @Override
    public void addItem(ProductionBuildingModule content, String fileName) {
        put(content.getId(), content);
    }
}
