package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductionOrderConverterTest {
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID PRODUCTION_REQUEST_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final Integer REQUESTED_AMOUNT = 3;
    private static final Integer STARTED_AMOUNT = 32;
    private static final String PRODUCTION_ORDER_ID_STRING = "production-order-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String PRODUCTION_REQUEST_ID_STRING = "production-request-id";
    private static final String CONSTRUCTION_AREA_ID_STRING = "construction-area-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ProductionOrderConverter underTest;

    @Test
    void convertDomain() {
        ProductionOrderModel model = new ProductionOrderModel();
        model.setId(PRODUCTION_ORDER_ID);
        model.setGameId(GAME_ID);
        model.setProductionRequestId(PRODUCTION_REQUEST_ID);
        model.setConstructionAreaId(CONSTRUCTION_AREA_ID);
        model.setResourceDataId(RESOURCE_DATA_ID);
        model.setRequestedAmount(REQUESTED_AMOUNT);
        model.setStartedAmount(STARTED_AMOUNT);

        given(uuidConverter.convertDomain(PRODUCTION_ORDER_ID)).willReturn(PRODUCTION_ORDER_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(PRODUCTION_REQUEST_ID)).willReturn(PRODUCTION_REQUEST_ID_STRING);
        given(uuidConverter.convertDomain(CONSTRUCTION_AREA_ID)).willReturn(CONSTRUCTION_AREA_ID_STRING);

        assertThat(underTest.convertDomain(model))
            .returns(PRODUCTION_ORDER_ID_STRING, ProductionOrderEntity::getProductionOrderId)
            .returns(GAME_ID_STRING, ProductionOrderEntity::getGameId)
            .returns(PRODUCTION_REQUEST_ID_STRING, ProductionOrderEntity::getProductionRequestId)
            .returns(CONSTRUCTION_AREA_ID_STRING, ProductionOrderEntity::getConstructionAreaId)
            .returns(RESOURCE_DATA_ID, ProductionOrderEntity::getResourceDataId)
            .returns(REQUESTED_AMOUNT, ProductionOrderEntity::getRequestedAmount)
            .returns(STARTED_AMOUNT, ProductionOrderEntity::getStartedAmount);
    }

    @Test
    void convertEntity() {
        ProductionOrderEntity entity = ProductionOrderEntity.builder()
            .productionOrderId(PRODUCTION_ORDER_ID_STRING)
            .gameId(GAME_ID_STRING)
            .productionRequestId(PRODUCTION_REQUEST_ID_STRING)
            .constructionAreaId(CONSTRUCTION_AREA_ID_STRING)
            .resourceDataId(RESOURCE_DATA_ID)
            .requestedAmount(REQUESTED_AMOUNT)
            .startedAmount(STARTED_AMOUNT)
            .build();

        given(uuidConverter.convertEntity(PRODUCTION_ORDER_ID_STRING)).willReturn(PRODUCTION_ORDER_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(PRODUCTION_REQUEST_ID_STRING)).willReturn(PRODUCTION_REQUEST_ID);
        given(uuidConverter.convertEntity(CONSTRUCTION_AREA_ID_STRING)).willReturn(CONSTRUCTION_AREA_ID);

        assertThat(underTest.convertEntity(entity))
            .returns(PRODUCTION_ORDER_ID, ProductionOrderModel::getId)
            .returns(GameItemType.PRODUCTION_ORDER, GameItem::getType)
            .returns(GAME_ID, ProductionOrderModel::getGameId)
            .returns(PRODUCTION_REQUEST_ID, ProductionOrderModel::getProductionRequestId)
            .returns(CONSTRUCTION_AREA_ID, ProductionOrderModel::getConstructionAreaId)
            .returns(RESOURCE_DATA_ID, ProductionOrderModel::getResourceDataId)
            .returns(REQUESTED_AMOUNT, ProductionOrderModel::getRequestedAmount)
            .returns(STARTED_AMOUNT, ProductionOrderModel::getStartedAmount);
    }
}