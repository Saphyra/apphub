package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ProductionOrderLoader {
    private final GameItemLoader gameItemLoader;

    public List<ProductionOrder> load(UUID planetId) {
        return gameItemLoader.loadChildren(planetId, GameItemType.PRODUCTION_ORDER, ProductionOrderModel[].class)
            .stream()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    private ProductionOrder convert(ProductionOrderModel model) {
        return ProductionOrder.builder()
            .productionOrderId(model.getId())
            .location(model.getLocation())
            .locationType(LocationType.valueOf(model.getLocationType()))
            .assignee(model.getAssignee())
            .externalReference(model.getExternalReference())
            .dataId(model.getDataId())
            .amount(model.getAmount())
            .requiredWorkPoints(model.getRequiredWorkPoints())
            .currentWorkPoints(model.getCurrentWorkPoints())
            .build();
    }
}
