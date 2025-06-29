package com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProductionOrdersTest {
    private static final UUID PRODUCTION_REQUEST_ID_1 = UUID.randomUUID();
    private static final UUID PRODUCTION_REQUEST_ID_2 = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID_1 = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID_2 = UUID.randomUUID();
    private static final String RESOURCE_DATA_ID_1 = "resource-data-id-1";
    private static final String RESOURCE_DATA_ID_2 = "resource-data-id-2";
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();

    private final ProductionOrders underTest = new ProductionOrders();

    @Test
    void getByProductionRequestIdAndConstructionAreaIdAndResourceDataId() {
        ProductionOrder productionOrder1 = ProductionOrder.builder()
            .productionRequestId(PRODUCTION_REQUEST_ID_1)
            .constructionAreaId(CONSTRUCTION_AREA_ID_1)
            .resourceDataId(RESOURCE_DATA_ID_1)
            .build();
        underTest.add(productionOrder1);
        ProductionOrder productionOrder2 = ProductionOrder.builder()
            .productionRequestId(PRODUCTION_REQUEST_ID_2)
            .constructionAreaId(CONSTRUCTION_AREA_ID_1)
            .resourceDataId(RESOURCE_DATA_ID_1)
            .build();
        underTest.add(productionOrder2);
        ProductionOrder productionOrder3 = ProductionOrder.builder()
            .productionRequestId(PRODUCTION_REQUEST_ID_1)
            .constructionAreaId(CONSTRUCTION_AREA_ID_2)
            .resourceDataId(RESOURCE_DATA_ID_1)
            .build();
        underTest.add(productionOrder3);
        ProductionOrder productionOrder4 = ProductionOrder.builder()
            .productionRequestId(PRODUCTION_REQUEST_ID_1)
            .constructionAreaId(CONSTRUCTION_AREA_ID_1)
            .resourceDataId(RESOURCE_DATA_ID_2)
            .build();
        underTest.add(productionOrder4);

        assertThat(underTest.getByProductionRequestIdAndConstructionAreaIdAndResourceDataId(PRODUCTION_REQUEST_ID_1, CONSTRUCTION_AREA_ID_1, RESOURCE_DATA_ID_1)).containsExactly(productionOrder1);
    }

    @Test
    void findByIdValidated() {
        ProductionOrder productionOrder = ProductionOrder.builder()
            .productionOrderId(PRODUCTION_ORDER_ID)
            .build();
        underTest.add(productionOrder);

        assertThat(underTest.findByIdValidated(PRODUCTION_ORDER_ID)).isEqualTo(productionOrder);
    }

    @Test
    void findByIdValidated_notFound() {
        ExceptionValidator.validateNotLoggedException(() -> underTest.findByIdValidated(PRODUCTION_ORDER_ID), HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }
}