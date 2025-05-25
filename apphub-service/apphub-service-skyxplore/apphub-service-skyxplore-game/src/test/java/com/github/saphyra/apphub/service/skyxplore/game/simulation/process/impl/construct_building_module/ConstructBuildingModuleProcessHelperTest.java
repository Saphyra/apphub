package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcessFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ConstructBuildingModuleProcessHelperTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final int REQUIRED_WORK_POINTS = 243;

    @Mock
    private ProductionOrderService productionOrderService;

    @Mock
    private UseAllocatedResourceService useAllocatedResourceService;

    @Mock
    private WorkProcessFactory workProcessFactory;

    @Mock
    private AllocationRemovalService allocationRemovalService;

    @InjectMocks
    private ConstructBuildingModuleProcessHelper underTest;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private GameData gameData;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction construction;

    @Mock
    private Processes processes;

    @Mock
    private WorkProcess process;

    @Mock
    private ProcessModel processModel;

    @Test
    void createProductionOrders() {
        underTest.createProductionOrders(progressDiff, gameData, PROCESS_ID, CONSTRUCTION_ID);

        then(productionOrderService).should().createProductionOrdersForReservedStorages(progressDiff, gameData, PROCESS_ID, CONSTRUCTION_ID);
    }

    @Test
    void startWork() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByIdValidated(CONSTRUCTION_ID)).willReturn(construction);
        given(construction.getLocation()).willReturn(LOCATION);
        given(construction.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(workProcessFactory.createForConstruction(gameData, PROCESS_ID, CONSTRUCTION_ID, LOCATION, REQUIRED_WORK_POINTS)).willReturn(List.of(process));
        given(process.toModel()).willReturn(processModel);
        given(gameData.getProcesses()).willReturn(processes);

        underTest.startWork(progressDiff, gameData, PROCESS_ID, CONSTRUCTION_ID);

        then(useAllocatedResourceService).should().resolveAllocations(progressDiff, gameData, LOCATION, CONSTRUCTION_ID);
        then(progressDiff).should().save(processModel);
        then(processes).should().add(process);
    }

    @Test
    void finishConstruction() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByIdValidated(CONSTRUCTION_ID)).willReturn(construction);

        underTest.finishConstruction(progressDiff, gameData, CONSTRUCTION_ID);

        then(allocationRemovalService).should().removeAllocationsAndReservations(progressDiff, gameData, CONSTRUCTION_ID);
        then(constructions).should().remove(construction);
        then(progressDiff).should().delete(CONSTRUCTION_ID, GameItemType.CONSTRUCTION);
    }
}