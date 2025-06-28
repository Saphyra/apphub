package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order.ProductionOrders;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductionOrderProcessConditionsTest {
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();

    @InjectMocks
    private ProductionOrderProcessConditions underTest;

    @Mock
    private GameData gameData;

    @Mock
    private ProductionOrders productionOrders;

    @Mock
    private ProductionOrder productionOrder;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Test
    void productionNeeded() {
        given(gameData.getProductionOrders()).willReturn(productionOrders);
        given(productionOrders.findByIdValidated(PRODUCTION_ORDER_ID)).willReturn(productionOrder);
        given(productionOrder.allStarted()).willReturn(false);

        assertThat(underTest.productionNeeded(gameData, PRODUCTION_ORDER_ID)).isTrue();
    }

    @Test
    void isFinished() {
        given(gameData.getProductionOrders()).willReturn(productionOrders);
        given(productionOrders.findByIdValidated(PRODUCTION_ORDER_ID)).willReturn(productionOrder);
        given(productionOrder.allStarted()).willReturn(true);
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(process.getStatus()).willReturn(ProcessStatus.DONE);

        assertThat(underTest.isFinished(gameData, PROCESS_ID, PRODUCTION_ORDER_ID)).isTrue();

        assertThat(underTest.isFinished(gameData, UUID.randomUUID(), PRODUCTION_ORDER_ID)).isTrue();
    }
}