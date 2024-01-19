package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
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
class DeconstructionProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final Integer PLANET_PRIORITY = 234;
    private static final Integer DECONSTRUCTION_PRIORITY = 324;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private DeconstructionProcessConditions conditions;

    @Mock
    private DeconstructionProcessHelper helper;

    @Mock
    private GameData gameData;

    @Mock
    private Game game;

    private DeconstructionProcess underTest;

    @Mock
    private Priorities priorities;

    @Mock
    private Priority priority;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @BeforeEach
    void setUp() {
        underTest = DeconstructionProcess.builder()
            .processId(PROCESS_ID)
            .status(ProcessStatus.CREATED)
            .deconstructionId(DECONSTRUCTION_ID)
            .gameData(gameData)
            .location(LOCATION)
            .applicationContextProxy(applicationContextProxy)
            .game(game)
            .build();
    }

    @Test
    void getExternalReference() {
        assertThat(underTest.getExternalReference()).isEqualTo(DECONSTRUCTION_ID);
    }

    @Test
    void getPriority() {
        given(gameData.getPriorities()).willReturn(priorities);
        given(priorities.findByLocationAndType(LOCATION, PriorityType.CONSTRUCTION)).willReturn(priority);
        given(priority.getValue()).willReturn(PLANET_PRIORITY);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByDeconstructionId(DECONSTRUCTION_ID)).willReturn(deconstruction);
        given(deconstruction.getPriority()).willReturn(DECONSTRUCTION_PRIORITY);

        assertThat(underTest.getPriority()).isEqualTo(PLANET_PRIORITY * DECONSTRUCTION_PRIORITY * GameConstants.PROCESS_PRIORITY_MULTIPLIER);
    }

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.DECONSTRUCTION);
    }

    @Test
    void work_buildingUtilized() {
        given(applicationContextProxy.getBean(DeconstructionProcessConditions.class)).willReturn(conditions);
        given(applicationContextProxy.getBean(DeconstructionProcessHelper.class)).willReturn(helper);

        given(conditions.buildingUtilized(gameData, DECONSTRUCTION_ID)).willReturn(true);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.CREATED);

        verify(helper, times(0)).startWork(progressDiff, gameData, PROCESS_ID, LOCATION, DECONSTRUCTION_ID);
    }

    @Test
    void work_startAndWaitingForWork() {
        given(applicationContextProxy.getBean(DeconstructionProcessConditions.class)).willReturn(conditions);
        given(applicationContextProxy.getBean(DeconstructionProcessHelper.class)).willReturn(helper);

        given(conditions.buildingUtilized(gameData, DECONSTRUCTION_ID)).willReturn(false);
        given(conditions.workFinished(gameData, PROCESS_ID)).willReturn(false);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);

        verify(helper).startWork(progressDiff, gameData, PROCESS_ID, LOCATION, DECONSTRUCTION_ID);
        verify(helper, times(0)).finishDeconstruction(progressDiff, gameData, PROCESS_ID);
    }

    @Test
    void work_finishDeconstruction() {
        given(applicationContextProxy.getBean(DeconstructionProcessConditions.class)).willReturn(conditions);
        given(applicationContextProxy.getBean(DeconstructionProcessHelper.class)).willReturn(helper);

        given(conditions.buildingUtilized(gameData, DECONSTRUCTION_ID)).willReturn(false);
        given(conditions.workFinished(gameData, PROCESS_ID)).willReturn(true);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);

        verify(helper).startWork(progressDiff, gameData, PROCESS_ID, LOCATION, DECONSTRUCTION_ID);
        verify(helper).finishDeconstruction(progressDiff, gameData, DECONSTRUCTION_ID);
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
        assertThat(result.getProcessType()).isEqualTo(ProcessType.DECONSTRUCTION);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getExternalReference()).isEqualTo(DECONSTRUCTION_ID);
    }
}