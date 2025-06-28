package com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProductionOrderFactoryTest {
    private static final UUID PRODUCTION_REQUEST_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final int REQUESTED_AMOUNT = 24;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ProductionOrderConverter productionOrderConverter;

    @InjectMocks
    private ProductionOrderFactory underTest;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private GameData gameData;

    @Mock
    private ProductionOrders productionOrders;

    @Mock
    private ProductionOrderModel model;

    @Test
    void save() {
        given(idGenerator.randomUuid()).willReturn(PRODUCTION_ORDER_ID);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(productionOrderConverter.toModel(eq(GAME_ID), any())).willReturn(model);
        given(gameData.getProductionOrders()).willReturn(productionOrders);

        ProductionOrder result = underTest.save(progressDiff, gameData, PRODUCTION_REQUEST_ID, CONSTRUCTION_AREA_ID, RESOURCE_DATA_ID, REQUESTED_AMOUNT);

        then(progressDiff).should().save(model);
        then(productionOrders).should().add(result);

        assertThat(result)
            .returns(PRODUCTION_ORDER_ID, ProductionOrder::getProductionOrderId)
            .returns(PRODUCTION_REQUEST_ID, ProductionOrder::getProductionRequestId)
            .returns(CONSTRUCTION_AREA_ID, ProductionOrder::getConstructionAreaId)
            .returns(RESOURCE_DATA_ID, ProductionOrder::getResourceDataId)
            .returns(REQUESTED_AMOUNT, ProductionOrder::getRequestedAmount);
    }
}