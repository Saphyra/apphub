package com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductionOrderFactory {
    private final IdGenerator idGenerator;
    private final ProductionOrderConverter productionOrderConverter;

    public ProductionOrder save(GameProgressDiff progressDiff, GameData gameData, UUID productionRequestId, UUID constructionAreaId, String resourceDataId, int requestedAmount) {
        ProductionOrder productionOrder = ProductionOrder.builder()
            .productionOrderId(idGenerator.randomUuid())
            .productionRequestId(productionRequestId)
            .constructionAreaId(constructionAreaId)
            .resourceDataId(resourceDataId)
            .requestedAmount(requestedAmount)
            .build();

        progressDiff.save(productionOrderConverter.toModel(gameData.getGameId(), productionOrder));
        gameData.getProductionOrders()
            .add(productionOrder);

        return productionOrder;
    }
}
