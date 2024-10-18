package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.construction_area;

import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ConstructionAreaService extends ValidationAbstractDataService<String, ConstructionArea> {
    public ConstructionAreaService(ContentLoaderFactory contentLoaderFactory, DataValidator<Map<String, ConstructionArea>> validator) {
        super("/data/building/construction_area", contentLoaderFactory, validator);
    }

    @Override
    public void init() {
        load(ConstructionArea.class);
    }

    @Override
    public void addItem(ConstructionArea content, String fileName) {
        put(content.getId(), content);
    }
}
