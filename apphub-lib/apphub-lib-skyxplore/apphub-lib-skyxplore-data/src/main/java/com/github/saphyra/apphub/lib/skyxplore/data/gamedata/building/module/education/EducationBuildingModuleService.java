package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.education;

import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import org.springframework.stereotype.Component;

@Component
public class EducationBuildingModuleService extends ValidationAbstractDataService<String, EducationBuildingModuleData> {
    EducationBuildingModuleService(ContentLoaderFactory contentLoaderFactory, EducationBuildingModuleValidator validator) {
        super("/data/building/module/education", contentLoaderFactory, validator);
    }

    @Override
    public void init() {
        load(EducationBuildingModuleData.class);
    }

    @Override
    public void addItem(EducationBuildingModuleData content, String fileName) {
        put(content.getId(), content);
    }
}
