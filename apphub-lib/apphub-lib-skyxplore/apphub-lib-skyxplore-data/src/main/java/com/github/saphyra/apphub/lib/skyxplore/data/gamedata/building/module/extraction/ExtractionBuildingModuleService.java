package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.extraction;

import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import org.springframework.stereotype.Component;

@Component
public class ExtractionBuildingModuleService extends ValidationAbstractDataService<String, ExtractionBuildingModuleData> {
    public ExtractionBuildingModuleService(ContentLoaderFactory contentLoaderFactory, ExtractionBuildingModuleValidator validator) {
        super("/data/building/module/extraction", contentLoaderFactory, validator);
    }

    @Override
    public void init() {
        load(ExtractionBuildingModuleData.class);
    }

    @Override
    public void addItem(ExtractionBuildingModuleData content, String fileName) {
        put(content.getId(), content);
    }
}
