package com.github.saphyra.apphub.service.skyxplore.game.common.converter.model;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
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
public class ProductionOrderToModelConverter {
    public List<ProductionOrderModel> convert(List<ProductionOrder> orders, UUID gameId) {
        return orders.stream()
            .map(productionOrder -> convert(productionOrder, gameId))
            .collect(Collectors.toList());
    }

    public ProductionOrderModel convert(ProductionOrder order, UUID gameId) {
        ProductionOrderModel model = new ProductionOrderModel();
        model.setId(order.getProductionOrderId());
        model.setGameId(gameId);
        model.setType(GameItemType.PRODUCTION_ORDER);
        model.setAssignee(order.getAssignee());
        model.setExternalReference(order.getExternalReference());
        model.setDataId(order.getDataId());
        model.setAmount(order.getAmount());
        model.setRequiredWorkPoints(order.getRequiredWorkPoints());
        model.setCurrentWorkPoints(order.getCurrentWorkPoints());

        return model;
    }
}
