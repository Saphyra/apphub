package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.construction_area;

import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ConstructionAreaDataService extends ValidationAbstractDataService<String, ConstructionAreaData> {
    public ConstructionAreaDataService(ContentLoaderFactory contentLoaderFactory, DataValidator<Map<String, ConstructionAreaData>> validator) {
        super("/data/building/construction_area", contentLoaderFactory, validator);
    }

    @Override
    public void init() {
        load(ConstructionAreaData.class);
    }

    @Override
    public void addItem(ConstructionAreaData content, String fileName) {
        put(content.getId(), content);
    }
}
