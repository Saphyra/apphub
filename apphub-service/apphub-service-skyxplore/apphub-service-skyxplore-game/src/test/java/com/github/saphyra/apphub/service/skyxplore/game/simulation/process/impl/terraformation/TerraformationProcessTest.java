package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.terraformation;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priorities;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priority;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
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
class TerraformationProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID TERRAFORMATION_ID = UUID.randomUUID();
    private static final Integer PLANET_PRIORITY = 345;
    private static final Integer TERRAFORMATION_PRIORITY = 35;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private GameData gameData;

    @Mock
    private TerraformationProcessConditions conditions;

    @Mock
    private TerraformationProcessHelper helper;

    @Mock
    private Game game;

    private TerraformationProcess underTest;

    @Mock
    private Priorities priorities;

    @Mock
    private Priority priority;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction terraformation;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @BeforeEach
    void setUp() {
        underTest = TerraformationProcess.builder()
            .processId(PROCESS_ID)
            .status(ProcessStatus.CREATED)
            .gameData(gameData)
            .location(LOCATION)
            .terraformationId(TERRAFORMATION_ID)
            .applicationContextProxy(applicationContextProxy)
            .game(game)
            .build();
    }

    @Test
    void getExternalReference() {
        assertThat(underTest.getExternalReference()).isEqualTo(TERRAFORMATION_ID);
    }

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.TERRAFORMATION);
    }

    @Test
    void getPriority() {
        given(gameData.getPriorities()).willReturn(priorities);
        given(priorities.findByLocationAndType(LOCATION, PriorityType.CONSTRUCTION)).willReturn(priority);
        given(priority.getValue()).willReturn(PLANET_PRIORITY);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByIdValidated(TERRAFORMATION_ID)).willReturn(terraformation);
        given(terraformation.getPriority()).willReturn(TERRAFORMATION_PRIORITY);

        assertThat(underTest.getPriority()).isEqualTo(PLANET_PRIORITY * TERRAFORMATION_PRIORITY * GameConstants.PROCESS_PRIORITY_MULTIPLIER);
    }

    @Test
    void work_waitForProductionOrders() {
        given(applicationContextProxy.getBean(TerraformationProcessConditions.class)).willReturn(conditions);
        given(applicationContextProxy.getBean(TerraformationProcessHelper.class)).willReturn(helper);

        given(conditions.productionOrdersComplete(gameData, PROCESS_ID)).willReturn(false);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);

        verify(conditions, times(0)).hasWorkProcesses(gameData, PROCESS_ID);
        verify(helper).createProductionOrders(progressDiff, gameData, PROCESS_ID, TERRAFORMATION_ID);
    }

    @Test
    void work_waitForWork() {
        given(applicationContextProxy.getBean(TerraformationProcessConditions.class)).willReturn(conditions);
        given(applicationContextProxy.getBean(TerraformationProcessHelper.class)).willReturn(helper);

        given(conditions.productionOrdersComplete(gameData, PROCESS_ID)).willReturn(true);
        given(conditions.hasWorkProcesses(gameData, PROCESS_ID)).willReturn(false);
        given(conditions.workFinished(gameData, PROCESS_ID)).willReturn(false);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);

        verify(helper).startWork(progressDiff, gameData, PROCESS_ID, TERRAFORMATION_ID);
        verify(helper).createProductionOrders(progressDiff, gameData, PROCESS_ID, TERRAFORMATION_ID);
        verify(helper, times(0)).finishTerraformation(progressDiff, gameData, TERRAFORMATION_ID);
    }

    @Test
    void work_finishTerraformation() {
        given(applicationContextProxy.getBean(TerraformationProcessConditions.class)).willReturn(conditions);
        given(applicationContextProxy.getBean(TerraformationProcessHelper.class)).willReturn(helper);

        given(conditions.productionOrdersComplete(gameData, PROCESS_ID)).willReturn(true);
        given(conditions.hasWorkProcesses(gameData, PROCESS_ID)).willReturn(true);
        given(conditions.workFinished(gameData, PROCESS_ID)).willReturn(true);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);

        verify(helper, times(0)).startWork(progressDiff, gameData, PROCESS_ID, TERRAFORMATION_ID);
        verify(helper).createProductionOrders(progressDiff, gameData, PROCESS_ID, TERRAFORMATION_ID);
        verify(helper).finishTerraformation(progressDiff, gameData, TERRAFORMATION_ID);
    }

    @Test
    void cleanup() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.cleanup();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);

        verify(process).cleanup();
        verify(progressDiff).save(underTest.toModel());
    }

    @Test
    void toModel() {
        given(gameData.getGameId()).willReturn(GAME_ID);

        ProcessModel result = underTest.toModel();

        assertThat(result.getId()).isEqualTo(PROCESS_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.PROCESS);
        assertThat(result.getProcessType()).isEqualTo(ProcessType.TERRAFORMATION);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getExternalReference()).isEqualTo(TERRAFORMATION_ID);
    }
}