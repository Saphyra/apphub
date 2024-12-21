package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.construction_area;

import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@NoArgsConstructor
public class ConstructionAreaDataService extends ValidationAbstractDataService<String, ConstructionAreaData> {
    @Autowired
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
