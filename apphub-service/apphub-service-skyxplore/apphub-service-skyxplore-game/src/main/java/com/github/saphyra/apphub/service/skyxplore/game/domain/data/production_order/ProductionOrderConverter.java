package com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ProductionOrderConverter {
    public ProductionOrderModel toModel(UUID gameId, ProductionOrder productionOrder) {
        ProductionOrderModel model = new ProductionOrderModel();
        model.setId(productionOrder.getProductionOrderId());
        model.setGameId(gameId);
        model.setType(GameItemType.PRODUCTION_ORDER);
        model.setProductionRequestId(productionOrder.getProductionRequestId());
        model.setConstructionAreaId(productionOrder.getConstructionAreaId());
        model.setResourceDataId(productionOrder.getResourceDataId());
        model.setRequestedAmount(productionOrder.getRequestedAmount());
        model.setStartedAmount(productionOrder.getStartedAmount());

        return model;
    }
}
