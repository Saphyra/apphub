package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.production;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.Production;
import org.springframework.stereotype.Component;

@Component
public class ProductionBuildingModuleDataService extends ValidationAbstractDataService<String, ProductionBuildingModuleData> {
    public ProductionBuildingModuleDataService(ContentLoaderFactory contentLoaderFactory, ProductionBuildingModuleValidator validator) {
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

    public BiWrapper<ProductionBuildingModuleData, Production> findProducerFor(String resourceDataId) {
        return values()
            .stream()
            .filter(buildingModuleData -> buildingModuleData.canProduce(resourceDataId))
            .findAny()
            .map(buildingModuleData -> new BiWrapper<>(buildingModuleData, buildingModuleData.findProductionForResource(resourceDataId)))
            .orElseThrow(() -> new IllegalArgumentException("No producer buildingModule found for resource " + resourceDataId));
    }
}
