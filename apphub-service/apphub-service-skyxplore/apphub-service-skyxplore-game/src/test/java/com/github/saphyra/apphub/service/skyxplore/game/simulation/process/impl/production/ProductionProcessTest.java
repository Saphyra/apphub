package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module_allocation.BuildingModuleAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module_allocation.BuildingModuleAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
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
class ProductionProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final int AMOUNT = 10;
    private static final int PRIORITY = 1000;
    private static final UUID BUILDING_MODULE_ALLOCATION_ID = UUID.randomUUID();
    private static final String PRODUCTION_ORDER_ID_STRING = "production-order-id";
    private static final String BUILDING_MODULE_ID_STRING = "building-module-id";
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private ProductionProcessHelper helper;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private UuidConverter uuidConverter;

    private ProductionProcess underTest;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Mock
    private BuildingModuleAllocations buildingModuleAllocations;

    @Mock
    private BuildingModuleAllocation buildingModuleAllocation;

    @BeforeEach
    void setUp() {
        underTest = ProductionProcess.builder()
            .processId(PROCESS_ID)
            .status(ProcessStatus.CREATED)
            .productionOrderId(PRODUCTION_ORDER_ID)
            .buildingModuleId(BUILDING_MODULE_ID)
            .amount(AMOUNT)
            .externalReference(EXTERNAL_REFERENCE)
            .location(LOCATION)
            .applicationContextProxy(applicationContextProxy)
            .game(game)
            .build();
    }

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.PRODUCTION);
    }

    @Test
    void getPriority() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.findByIdValidated(EXTERNAL_REFERENCE)).willReturn(process);
        given(process.getPriority()).willReturn(PRIORITY);

        assertThat(underTest.getPriority()).isEqualTo(PRIORITY + 1);
    }

    @Test
    void work_waitingToBeFinished() {
        given(applicationContextProxy.getBean(ProductionProcessHelper.class)).willReturn(helper);
        given(game.getData()).willReturn(gameData);
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(process.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);

        then(helper).should().createWorkProcess(game, LOCATION, PROCESS_ID, PRODUCTION_ORDER_ID, AMOUNT);
    }

    @Test
    void work_resourceIsNotProduced() {
        given(applicationContextProxy.getBean(ProductionProcessHelper.class)).willReturn(helper);
        given(game.getData()).willReturn(gameData);
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(process.getStatus()).willReturn(ProcessStatus.DONE);
        given(helper.resourcesProduced(game, LOCATION, PRODUCTION_ORDER_ID, AMOUNT)).willReturn(false);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);

        then(helper).should().createWorkProcess(game, LOCATION, PROCESS_ID, PRODUCTION_ORDER_ID, AMOUNT);
    }

    @Test
    void work_done() {
        given(applicationContextProxy.getBean(ProductionProcessHelper.class)).willReturn(helper);
        given(game.getData()).willReturn(gameData);
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(process.getStatus()).willReturn(ProcessStatus.DONE);
        given(helper.resourcesProduced(game, LOCATION, PRODUCTION_ORDER_ID, AMOUNT)).willReturn(true);
        given(game.getData().getBuildingModuleAllocations()).willReturn(buildingModuleAllocations);
        given(buildingModuleAllocations.findByProcessId(PROCESS_ID)).willReturn(Optional.of(buildingModuleAllocation));
        given(buildingModuleAllocation.getBuildingModuleAllocationId()).willReturn(BUILDING_MODULE_ALLOCATION_ID);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.DONE);

        then(helper).should().createWorkProcess(game, LOCATION, PROCESS_ID, PRODUCTION_ORDER_ID, AMOUNT);
        then(buildingModuleAllocations).should().remove(buildingModuleAllocation);
        then(progressDiff).should().delete(BUILDING_MODULE_ALLOCATION_ID, GameItemType.BUILDING_MODULE_ALLOCATION);
    }

    @Test
    void cleanup() {
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(game.getData()).willReturn(gameData);

        given(gameData.getBuildingModuleAllocations()).willReturn(buildingModuleAllocations);
        given(buildingModuleAllocations.findByProcessId(PROCESS_ID)).willReturn(Optional.of(buildingModuleAllocation));
        given(buildingModuleAllocation.getBuildingModuleAllocationId()).willReturn(BUILDING_MODULE_ALLOCATION_ID);

        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);

        underTest.cleanup();

        then(buildingModuleAllocations).should().remove(buildingModuleAllocation);
        then(progressDiff).should().delete(BUILDING_MODULE_ALLOCATION_ID, GameItemType.BUILDING_MODULE_ALLOCATION);
        then(process).should().cleanup();
        then(progressDiff).should().save(underTest.toModel());

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);
    }

    @Test
    void toModel() {
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);
        given(uuidConverter.convertDomain(PRODUCTION_ORDER_ID)).willReturn(PRODUCTION_ORDER_ID_STRING);
        given(uuidConverter.convertDomain(BUILDING_MODULE_ID)).willReturn(BUILDING_MODULE_ID_STRING);
        given(game.getGameId()).willReturn(GAME_ID);

        assertThat(underTest.toModel())
            .returns(PROCESS_ID, GameItem::getId)
            .returns(GAME_ID, GameItem::getGameId)
            .returns(GameItemType.PROCESS, GameItem::getType)
            .returns(ProcessType.PRODUCTION, ProcessModel::getProcessType)
            .returns(ProcessStatus.CREATED, ProcessModel::getStatus)
            .returns(LOCATION, ProcessModel::getLocation)
            .returns(EXTERNAL_REFERENCE, ProcessModel::getExternalReference)
            .returns(PRODUCTION_ORDER_ID_STRING, processModel -> processModel.getData().get(ProcessParamKeys.PRODUCTION_ORDER_ID))
            .returns(BUILDING_MODULE_ID_STRING, processModel -> processModel.getData().get(ProcessParamKeys.BUILDING_MODULE_ID))
            .returns(String.valueOf(AMOUNT), processModel -> processModel.getData().get(ProcessParamKeys.AMOUNT));
    }
}