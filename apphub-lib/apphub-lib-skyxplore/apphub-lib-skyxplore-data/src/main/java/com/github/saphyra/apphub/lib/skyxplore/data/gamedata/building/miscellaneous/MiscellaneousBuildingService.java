package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.miscellaneous;

import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@Slf4j
public class MiscellaneousBuildingService extends ValidationAbstractDataService<String, MiscellaneousBuilding> {
    public MiscellaneousBuildingService(ContentLoaderFactory contentLoaderFactory, MiscellaneousBuildingValidator buildingValidator) {
        super("/data/building/miscellaneous", contentLoaderFactory, buildingValidator);
    }

    @Override
    @PostConstruct
    public void init() {
        super.load(MiscellaneousBuilding.class);
        log.debug("MiscellaneousBuildingService: {}", this);
    }

    @Override
    public void addItem(MiscellaneousBuilding content, String fileName) {
        put(content.getId(), content);
    }
}
