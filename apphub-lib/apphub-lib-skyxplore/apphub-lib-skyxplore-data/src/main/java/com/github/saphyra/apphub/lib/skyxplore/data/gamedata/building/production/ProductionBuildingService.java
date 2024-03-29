package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production;

import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@Slf4j
public class ProductionBuildingService extends ValidationAbstractDataService<String, ProductionBuildingData> {
    public ProductionBuildingService(ContentLoaderFactory contentLoaderFactory, ProductionBuildingValidator productionBuildingValidator) {
        super("/data/building/production", contentLoaderFactory, productionBuildingValidator);
    }

    @Override
    @PostConstruct
    public void init() {
        super.load(ProductionBuildingData.class);
        log.debug("ProductionBuildingService: {}", this);
    }

    @Override
    public void addItem(ProductionBuildingData content, String fileName) {
        put(content.getId(), content);
    }
}
