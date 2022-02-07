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
public class WorkOnOrderServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();

    @Mock
    private ProcessProductionOrderRequirementsService processProductionOrderRequirementsService;

    @Mock
    private ProductionOrderRequirementsMetCalculator productionOrderRequirementsMetCalculator;

    @Mock
    private ResourceAssembler resourceAssembler;

    @Mock
    private DeliverProductionOrderService deliverProductionOrderService;

    @Mock
    private ProductionOrderProcessingService productionOrderProcessingService;

    @InjectMocks
    private WorkOnOrderService underTest;

    @Mock
    private Planet planet;

    @Mock
    private ProductionOrder order;

    @Test
    public void workOnOrder() {
        given(order.getProductionOrderId()).willReturn(PRODUCTION_ORDER_ID);
        given(order.getCurrentWorkPoints()).willReturn(1);
        given(order.getRequiredWorkPoints()).willReturn(1);

        given(productionOrderRequirementsMetCalculator.areRequirementsMet(planet, PRODUCTION_ORDER_ID)).willReturn(true);

        underTest.workOnOrder(GAME_ID, planet, order, productionOrderProcessingService);

        verify(processProductionOrderRequirementsService).processRequirements(GAME_ID, planet, order, productionOrderProcessingService);
        verify(resourceAssembler).assembleResource(GAME_ID, planet, order);
        verify(deliverProductionOrderService).deliverOrder(GAME_ID, planet, order);
    }
}