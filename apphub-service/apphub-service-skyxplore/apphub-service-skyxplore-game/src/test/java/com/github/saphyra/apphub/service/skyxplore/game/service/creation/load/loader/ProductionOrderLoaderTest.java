package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ProductionOrderLoaderTest {
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID ASSIGNEE = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 25;
    private static final Integer REQUIRED_WORK_POINTS = 247;
    private static final Integer CURRENT_WORK_POINTS = 2;

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private ProductionOrderLoader underTest;

    @Test
    public void load() {
        ProductionOrderModel model = new ProductionOrderModel();
        model.setId(PRODUCTION_ORDER_ID);
        model.setGameId(GAME_ID);
        model.setLocation(LOCATION);
        model.setLocationType(LocationType.PLANET.name());
        model.setAssignee(ASSIGNEE);
        model.setExternalReference(EXTERNAL_REFERENCE);
        model.setDataId(DATA_ID);
        model.setAmount(AMOUNT);
        model.setRequiredWorkPoints(REQUIRED_WORK_POINTS);
        model.setCurrentWorkPoints(CURRENT_WORK_POINTS);

        given(gameItemLoader.loadChildren(PLANET_ID, GameItemType.PRODUCTION_ORDER, ProductionOrderModel[].class)).willReturn(List.of(model));

        Set<ProductionOrder> result = underTest.load(PLANET_ID);

        assertThat(result).hasSize(1);
        ProductionOrder productionOrder = result.iterator().next();
        assertThat(productionOrder.getProductionOrderId()).isEqualTo(PRODUCTION_ORDER_ID);
        assertThat(productionOrder.getLocation()).isEqualTo(LOCATION);
        assertThat(productionOrder.getLocationType()).isEqualTo(LocationType.PLANET);
        assertThat(productionOrder.getAssignee()).isEqualTo(ASSIGNEE);
        assertThat(productionOrder.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(productionOrder.getDataId()).isEqualTo(DATA_ID);
        assertThat(productionOrder.getAmount()).isEqualTo(AMOUNT);
        assertThat(productionOrder.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(productionOrder.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
    }
}