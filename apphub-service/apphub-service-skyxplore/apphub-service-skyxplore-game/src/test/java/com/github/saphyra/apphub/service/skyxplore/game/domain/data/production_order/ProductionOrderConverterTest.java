package com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProductionOrderConverterTest {
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final UUID PRODUCTION_REQUEST_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final int REQUESTED_AMOUNT = 243;
    private static final int STARTED_AMOUNT = 3;
    private static final UUID GAME_ID = UUID.randomUUID();

    @InjectMocks
    private ProductionOrderConverter underTest;

    @Test
    void toModel() {
        ProductionOrder productionOrder = ProductionOrder.builder()
            .productionOrderId(PRODUCTION_ORDER_ID)
            .productionRequestId(PRODUCTION_REQUEST_ID)
            .constructionAreaId(CONSTRUCTION_AREA_ID)
            .requestedAmount(REQUESTED_AMOUNT)
            .startedAmount(STARTED_AMOUNT)
            .build();

        assertThat(underTest.toModel(GAME_ID, productionOrder))
            .returns(PRODUCTION_ORDER_ID, GameItem::getId)
            .returns(GameItemType.PRODUCTION_ORDER, GameItem::getType)
            .returns(GAME_ID, GameItem::getGameId)
            .returns(PRODUCTION_REQUEST_ID, ProductionOrderModel::getProductionRequestId)
            .returns(CONSTRUCTION_AREA_ID, ProductionOrderModel::getConstructionAreaId)
            .returns(REQUESTED_AMOUNT, ProductionOrderModel::getRequestedAmount)
            .returns(STARTED_AMOUNT, ProductionOrderModel::getStartedAmount);
    }
}