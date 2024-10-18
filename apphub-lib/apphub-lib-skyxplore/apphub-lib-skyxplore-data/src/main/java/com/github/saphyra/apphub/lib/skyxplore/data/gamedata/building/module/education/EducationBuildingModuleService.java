package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.education;

import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import org.springframework.stereotype.Component;

@Component
public class EducationBuildingModuleService extends ValidationAbstractDataService<String, EducationBuildingModule> {
    EducationBuildingModuleService(ContentLoaderFactory contentLoaderFactory, EducationBuildingModuleValidator validator) {
        super("/data/building/module/education", contentLoaderFactory, validator);
    }

    @Override
    public void init() {
        load(EducationBuildingModule.class);
    }

    @Override
    public void addItem(EducationBuildingModule content, String fileName) {
        put(content.getId(), content);
    }
}
