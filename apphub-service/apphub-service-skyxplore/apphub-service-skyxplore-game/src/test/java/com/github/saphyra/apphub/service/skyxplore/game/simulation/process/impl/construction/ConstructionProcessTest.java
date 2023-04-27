package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priorities;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priority;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConstructionProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final Integer PLANET_PRIORITY = 3124;
    private static final Integer CONSTRUCTION_PRIORITY = 23;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private GameData gameData;

    @Mock
    private ConstructionProcessHelper helper;

    @Mock
    private ConstructionProcessConditions conditions;

    private ConstructionProcess underTest;

    @Mock
    private Priorities priorities;

    @Mock
    private Priority priority;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction construction;

    @Mock
    private SyncCache syncCache;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @BeforeEach
    void setUp() {
        underTest = ConstructionProcess.builder()
            .processId(PROCESS_ID)
            .status(ProcessStatus.CREATED)
            .gameData(gameData)
            .constructionId(CONSTRUCTION_ID)
            .location(LOCATION)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Test
    void getExternalReference() {
        assertThat(underTest.getExternalReference()).isEqualTo(CONSTRUCTION_ID);
    }

    @Test
    void getPriority() {
        given(gameData.getPriorities()).willReturn(priorities);
        given(priorities.findByLocationAndType(LOCATION, PriorityType.CONSTRUCTION)).willReturn(priority);
        given(priority.getValue()).willReturn(PLANET_PRIORITY);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByConstructionIdValidated(CONSTRUCTION_ID)).willReturn(construction);
        given(construction.getPriority()).willReturn(CONSTRUCTION_PRIORITY);

        assertThat(underTest.getPriority()).isEqualTo(PLANET_PRIORITY * CONSTRUCTION_PRIORITY * GameConstants.PROCESS_PRIORITY_MULTIPLIER);
    }

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.CONSTRUCTION);
    }

    @Test
    void work_createProductionOrdersAndWaitForThem() {
        given(applicationContextProxy.getBean(ConstructionProcessHelper.class)).willReturn(helper);
        given(applicationContextProxy.getBean(ConstructionProcessConditions.class)).willReturn(conditions);

        given(conditions.productionOrdersComplete(gameData, PROCESS_ID)).willReturn(false);

        underTest.work(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);

        verify(helper).createProductionOrders(syncCache, gameData, PROCESS_ID, CONSTRUCTION_ID);
        verify(conditions, times(0)).hasWorkProcesses(gameData, PROCESS_ID);
    }

    @Test
    void work_createWorkProcessesAndWaitForThem() {
        given(applicationContextProxy.getBean(ConstructionProcessHelper.class)).willReturn(helper);
        given(applicationContextProxy.getBean(ConstructionProcessConditions.class)).willReturn(conditions);

        given(conditions.productionOrdersComplete(gameData, PROCESS_ID)).willReturn(true);
        given(conditions.hasWorkProcesses(gameData, PROCESS_ID)).willReturn(false);
        given(conditions.workFinished(gameData, PROCESS_ID)).willReturn(false);

        underTest.work(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);

        verify(helper).startWork(syncCache, gameData, PROCESS_ID, CONSTRUCTION_ID);
        verify(helper, times(0)).finishConstruction(syncCache, gameData, CONSTRUCTION_ID);
    }

    @Test
    void work_finishConstruction() {
        given(applicationContextProxy.getBean(ConstructionProcessHelper.class)).willReturn(helper);
        given(applicationContextProxy.getBean(ConstructionProcessConditions.class)).willReturn(conditions);

        given(conditions.productionOrdersComplete(gameData, PROCESS_ID)).willReturn(true);
        given(conditions.hasWorkProcesses(gameData, PROCESS_ID)).willReturn(true);
        given(conditions.workFinished(gameData, PROCESS_ID)).willReturn(true);

        underTest.work(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);

        verify(helper).finishConstruction(syncCache, gameData, CONSTRUCTION_ID);
    }

    @Test
    void cleanup() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));

        underTest.cleanup(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);

        verify(process).cleanup(syncCache);
        verify(syncCache).saveGameItem(underTest.toModel());
    }

    @Test
    void toModel() {
        given(gameData.getGameId()).willReturn(GAME_ID);

        ProcessModel result = underTest.toModel();

        assertThat(result.getId()).isEqualTo(PROCESS_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.PROCESS);
        assertThat(result.getProcessType()).isEqualTo(ProcessType.CONSTRUCTION);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getExternalReference()).isEqualTo(CONSTRUCTION_ID);
    }
}