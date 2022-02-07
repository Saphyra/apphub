package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProductionOrderProcessingServiceTest {
    private static final UUID ASSIGNEE = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private AssignBuildingToOrderService assignBuildingToOrderService;

    @Mock
    private WorkOnOrderService workOnOrderService;

    @InjectMocks
    private ProductionOrderProcessingService underTest;

    @Mock
    private Planet planet;

    @Mock
    private ProductionOrder order;

    @Test
    public void processOrder() {
        given(order.getAssignee())
            .willReturn(null)
            .willReturn(ASSIGNEE);

        underTest.processOrder(GAME_ID, planet, order);

        verify(assignBuildingToOrderService).assignOrder(GAME_ID, planet, order, underTest);
        verify(workOnOrderService).workOnOrder(GAME_ID, planet, order, underTest);
    }
}