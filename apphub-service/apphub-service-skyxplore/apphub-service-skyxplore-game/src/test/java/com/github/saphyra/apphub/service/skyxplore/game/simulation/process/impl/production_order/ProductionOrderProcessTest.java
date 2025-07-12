package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order.ProductionOrders;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProductionOrderProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final int PRIORITY = 3;

    @Mock
    private GameData gameData;

    @Mock
    private Game game;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private ProductionOrderProcessHelper helper;

    @Mock
    private ProductionOrderProcessConditions conditions;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private AllocationRemovalService allocationRemovalService;

    @Mock
    private UuidConverter uuidConverter;

    private ProductionOrderProcess underTest;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Mock
    private ProductionOrders productionOrders;

    @Mock
    private ProductionOrder productionOrder;

    @BeforeEach
    void setUp() {
        underTest = ProductionOrderProcess.builder()
            .processId(PROCESS_ID)
            .status(ProcessStatus.CREATED)
            .externalReference(EXTERNAL_REFERENCE)
            .location(LOCATION)
            .applicationContextProxy(applicationContextProxy)
            .game(game)
            .productionOrderId(PRODUCTION_ORDER_ID)
            .build();
    }

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.PRODUCTION_ORDER);
    }

    @Test
    void getPriority() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.findByIdValidated(EXTERNAL_REFERENCE)).willReturn(process);
        given(process.getPriority()).willReturn(PRIORITY);
        given(game.getData()).willReturn(gameData);

        assertThat(underTest.getPriority()).isEqualTo(PRIORITY + 1);
    }

    @Test
    void work_productionNeeded() {
        given(applicationContextProxy.getBean(ProductionOrderProcessHelper.class)).willReturn(helper);
        given(applicationContextProxy.getBean(ProductionOrderProcessConditions.class)).willReturn(conditions);
        given(conditions.productionNeeded(gameData, PRODUCTION_ORDER_ID)).willReturn(true);
        given(conditions.isFinished(gameData, PROCESS_ID, PRODUCTION_ORDER_ID)).willReturn(false);
        given(game.getData()).willReturn(gameData);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);

        then(helper).should().orderResources(game, LOCATION, PROCESS_ID, PRODUCTION_ORDER_ID);
        then(helper).should().tryProduce(game, LOCATION, PROCESS_ID, PRODUCTION_ORDER_ID);
    }

    @Test
    void work_finished() {
        given(applicationContextProxy.getBean(ProductionOrderProcessHelper.class)).willReturn(helper);
        given(applicationContextProxy.getBean(ProductionOrderProcessConditions.class)).willReturn(conditions);
        given(conditions.productionNeeded(gameData, PRODUCTION_ORDER_ID)).willReturn(true);
        given(conditions.isFinished(gameData, PROCESS_ID, PRODUCTION_ORDER_ID)).willReturn(true);
        given(game.getData()).willReturn(gameData);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.DONE);

        then(helper).should().orderResources(game, LOCATION, PROCESS_ID, PRODUCTION_ORDER_ID);
        then(helper).should().tryProduce(game, LOCATION, PROCESS_ID, PRODUCTION_ORDER_ID);
    }

    @Test
    void cleanup(){
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(applicationContextProxy.getBean(AllocationRemovalService.class)).willReturn(allocationRemovalService);
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(gameData.getProductionOrders()).willReturn(productionOrders);
        given(productionOrders.findById(PRODUCTION_ORDER_ID)).willReturn(Optional.of(productionOrder));
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);
        given(game.getData()).willReturn(gameData);

        underTest.cleanup();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);

        then(allocationRemovalService).should().removeAllocationsAndReservations(progressDiff, gameData, PROCESS_ID);
        then(process).should().cleanup();
        then(productionOrders).should().remove(productionOrder);
        then(progressDiff).should().delete(PRODUCTION_ORDER_ID, GameItemType.PRODUCTION_ORDER);
        then(progressDiff).should().save(underTest.toModel());
    }
}