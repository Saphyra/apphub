package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production;

import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class ProductionBuildingService extends ValidationAbstractDataService<String, ProductionBuilding> {
    public ProductionBuildingService(ContentLoaderFactory contentLoaderFactory, ProductionBuildingValidator productionBuildingValidator) {
        super("/data/building/production", contentLoaderFactory, productionBuildingValidator);
    }

    @Override
    @PostConstruct
    public void init() {
        super.load(ProductionBuilding.class);
        log.debug("ProductionBuildingService: {}", this);
    }

    @Override
    public void addItem(ProductionBuilding content, String fileName) {
        put(content.getId(), content);
    }
}
