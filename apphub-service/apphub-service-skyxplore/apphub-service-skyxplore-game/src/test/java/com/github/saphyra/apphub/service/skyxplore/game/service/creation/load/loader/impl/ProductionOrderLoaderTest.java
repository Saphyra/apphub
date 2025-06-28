package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order.ProductionOrders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProductionOrderLoaderTest {
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final UUID PRODUCTION_REQUEST_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final Integer REQUESTED_AMOUNT = 2;
    private static final Integer STARTED_AMOUNT = 134;

    @InjectMocks
    private ProductionOrderLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private ProductionOrders productionOrders;

    @Mock
    private ProductionOrder productionOrder;

    @Mock
    private ProductionOrderModel model;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.PRODUCTION_ORDER);
    }

    @Test
    void addToGameData() {
        given(gameData.getProductionOrders()).willReturn(productionOrders);

        underTest.addToGameData(gameData, List.of(productionOrder));

        then(productionOrders).should().addAll(List.of(productionOrder));
    }

    @Test
    void convert() {
        given(model.getId()).willReturn(PRODUCTION_ORDER_ID);
        given(model.getProductionRequestId()).willReturn(PRODUCTION_REQUEST_ID);
        given(model.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID);
        given(model.getResourceDataId()).willReturn(RESOURCE_DATA_ID);
        given(model.getRequestedAmount()).willReturn(REQUESTED_AMOUNT);
        given(model.getStartedAmount()).willReturn(STARTED_AMOUNT);

        assertThat(underTest.convert(model))
            .returns(PRODUCTION_ORDER_ID, ProductionOrder::getProductionOrderId)
            .returns(PRODUCTION_REQUEST_ID, ProductionOrder::getProductionRequestId)
            .returns(CONSTRUCTION_AREA_ID, ProductionOrder::getConstructionAreaId)
            .returns(RESOURCE_DATA_ID, ProductionOrder::getResourceDataId)
            .returns(REQUESTED_AMOUNT, ProductionOrder::getRequestedAmount)
            .returns(STARTED_AMOUNT, ProductionOrder::getStartedAmount);
    }
}