package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
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
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeconstructConstructionAreaProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final int PRIORITY_VALUE = 34;
    private static final Integer CONSTRUCTION_PRIORITY = 245;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private DeconstructConstructionAreaProcessConditions conditions;

    @Mock
    private DeconstructConstructionAreaProcessHelper processHelper;

    private DeconstructConstructionAreaProcess underTest;

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
        underTest = DeconstructConstructionAreaProcess.builder()
            .processId(PROCESS_ID)
            .deconstructionId(DECONSTRUCTION_ID)
            .status(ProcessStatus.CREATED)
            .location(LOCATION)
            .game(game)
            .gameData(gameData)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.DECONSTRUCT_CONSTRUCTION_AREA);
    }

    @Test
    void getPriority() {
        given(gameData.getPriorities()).willReturn(priorities);
        given(priorities.findByLocationAndType(LOCATION, PriorityType.CONSTRUCTION)).willReturn(priority);
        given(priority.getValue()).willReturn(PRIORITY_VALUE);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByIdValidated(DECONSTRUCTION_ID)).willReturn(deconstruction);
        given(deconstruction.getPriority()).willReturn(CONSTRUCTION_PRIORITY);

        assertThat(underTest.getPriority()).isEqualTo(PRIORITY_VALUE * CONSTRUCTION_PRIORITY * GameConstants.PROCESS_PRIORITY_MULTIPLIER);
    }

    @Test
    void work_modulesNotDeconstructed() {
        given(applicationContextProxy.getBean(DeconstructConstructionAreaProcessConditions.class)).willReturn(conditions);
        given(applicationContextProxy.getBean(DeconstructConstructionAreaProcessHelper.class)).willReturn(processHelper);
        given(conditions.modulesDeconstructed(gameData, CONSTRUCTION_AREA_ID)).willReturn(false);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByIdValidated(DECONSTRUCTION_ID)).willReturn(deconstruction);
        given(deconstruction.getExternalReference()).willReturn(CONSTRUCTION_AREA_ID);

        underTest.work();

        then(processHelper).should().initiateDeconstructModules(game, DECONSTRUCTION_ID);
        then(processHelper).shouldHaveNoMoreInteractions();
    }

    @Test
    void work_noWorkProcesses() {
        given(applicationContextProxy.getBean(DeconstructConstructionAreaProcessConditions.class)).willReturn(conditions);
        given(applicationContextProxy.getBean(DeconstructConstructionAreaProcessHelper.class)).willReturn(processHelper);
        given(conditions.hasWorkProcesses(gameData, PROCESS_ID)).willReturn(false);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(conditions.workFinished(gameData, PROCESS_ID)).willReturn(false);
        given(conditions.modulesDeconstructed(gameData, CONSTRUCTION_AREA_ID)).willReturn(true);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByIdValidated(DECONSTRUCTION_ID)).willReturn(deconstruction);
        given(deconstruction.getExternalReference()).willReturn(CONSTRUCTION_AREA_ID);

        underTest.work();

        then(processHelper).should().initiateDeconstructModules(game, DECONSTRUCTION_ID);
        then(processHelper).should().startWork(game, PROCESS_ID, DECONSTRUCTION_ID);
        then(processHelper).shouldHaveNoMoreInteractions();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
    }

    @Test
    void work_finish() {
        given(applicationContextProxy.getBean(DeconstructConstructionAreaProcessConditions.class)).willReturn(conditions);
        given(applicationContextProxy.getBean(DeconstructConstructionAreaProcessHelper.class)).willReturn(processHelper);
        given(conditions.hasWorkProcesses(gameData, PROCESS_ID)).willReturn(true);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(conditions.workFinished(gameData, PROCESS_ID)).willReturn(true);
        given(conditions.modulesDeconstructed(gameData, CONSTRUCTION_AREA_ID)).willReturn(true);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByIdValidated(DECONSTRUCTION_ID)).willReturn(deconstruction);
        given(deconstruction.getExternalReference()).willReturn(CONSTRUCTION_AREA_ID);

        underTest.work();

        then(processHelper).should().initiateDeconstructModules(game, DECONSTRUCTION_ID);
        then(processHelper).should().finishDeconstruction(progressDiff, gameData, DECONSTRUCTION_ID);
        then(processHelper).shouldHaveNoMoreInteractions();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);
    }

    @Test
    void cleanup() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.cleanup();

        then(process).should().cleanup();
        then(progressDiff).should().save(underTest.toModel());

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);
    }

    @Test
    void toModel() {
        given(gameData.getGameId()).willReturn(GAME_ID);

        assertThat(underTest.toModel())
            .returns(PROCESS_ID, GameItem::getId)
            .returns(GAME_ID, GameItem::getGameId)
            .returns(GameItemType.PROCESS, GameItem::getType)
            .returns(ProcessType.DECONSTRUCT_CONSTRUCTION_AREA, ProcessModel::getProcessType)
            .returns(ProcessStatus.CREATED, ProcessModel::getStatus)
            .returns(LOCATION, ProcessModel::getLocation)
            .returns(DECONSTRUCTION_ID, ProcessModel::getExternalReference);
    }
}