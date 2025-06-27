package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
class ProductionOrderLoader extends AutoLoader<ProductionOrderModel, ProductionOrder> {
    ProductionOrderLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.PRODUCTION_ORDER;
    }

    @Override
    protected Class<ProductionOrderModel[]> getArrayClass() {
        return ProductionOrderModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<ProductionOrder> items) {
        gameData.getProductionOrders()
            .addAll(items);
    }

    @Override
    protected ProductionOrder convert(ProductionOrderModel model) {
        return ProductionOrder.builder()
            .productionOrderId(model.getId())
            .productionRequestId(model.getProductionRequestId())
            .constructionAreaId(model.getConstructionAreaId())
            .resourceDataId(model.getResourceDataId())
            .requestedAmount(model.getRequestedAmount())
            .startedAmount(model.getStartedAmount())
            .build();
    }
}
