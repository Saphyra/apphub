package com.github.saphyra.apphub.service.skyxplore.game.common.converter.model;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ProductionOrderToModelConverterTest {
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final UUID ASSIGNEE = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int AMOUNT = 3214;
    private static final int REQUIRED_WORK_PINTS = 6537;
    private static final int CURRENT_WORK_POINTS = 26;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    @InjectMocks
    private ProductionOrderToModelConverter underTest;

    @Test
    public void convert() {
        ProductionOrder order = ProductionOrder.builder()
            .productionOrderId(PRODUCTION_ORDER_ID)
            .location(LOCATION)
            .locationType(LocationType.PRODUCTION)
            .assignee(ASSIGNEE)
            .externalReference(EXTERNAL_REFERENCE)
            .dataId(DATA_ID)
            .amount(AMOUNT)
            .requiredWorkPoints(REQUIRED_WORK_PINTS)
            .currentWorkPoints(CURRENT_WORK_POINTS)
            .build();

        ProductionOrderModel result = underTest.convert(order, GAME_ID);

        assertThat(result.getId()).isEqualTo(PRODUCTION_ORDER_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getProcessType()).isEqualTo(GameItemType.PRODUCTION_ORDER);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getLocationType()).isEqualTo(LocationType.PRODUCTION.name());
        assertThat(result.getAssignee()).isEqualTo(ASSIGNEE);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getAmount()).isEqualTo(AMOUNT);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_PINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
    }
}