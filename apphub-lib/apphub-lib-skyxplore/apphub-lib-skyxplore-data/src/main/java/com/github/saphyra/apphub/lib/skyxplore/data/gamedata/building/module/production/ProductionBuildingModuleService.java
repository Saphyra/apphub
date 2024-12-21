package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.production;

import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import org.springframework.stereotype.Component;

@Component
public class ProductionBuildingModuleService extends ValidationAbstractDataService<String, ProductionBuildingModuleData> {
    public ProductionBuildingModuleService(ContentLoaderFactory contentLoaderFactory, ProductionBuildingModuleValidator validator) {
        super("/data/building/module/production", contentLoaderFactory, validator);
    }

    @Override
    public void init() {
        load(ProductionBuildingModuleData.class);
    }

    @Override
    public void addItem(ProductionBuildingModuleData content, String fileName) {
        put(content.getId(), content);
    }
}
