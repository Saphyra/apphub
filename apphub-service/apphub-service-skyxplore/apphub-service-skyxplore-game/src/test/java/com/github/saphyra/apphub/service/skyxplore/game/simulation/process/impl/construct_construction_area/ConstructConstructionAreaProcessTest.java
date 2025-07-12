package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
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
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ConstructConstructionAreaProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final Integer BASE_PRIORITY = 2;
    private static final Integer CONSTRUCTION_PRIORITY = 3;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameData gameData;

    @Mock
    private Game game;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private ConstructConstructionAreaProcessConditions conditions;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private ConstructConstructionAreaProcessHelper helper;

    private ConstructConstructionAreaProcess underTest;

    @Mock
    private Priorities priorities;

    @Mock
    private Priority priority;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction construction;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @BeforeEach
    void setUp() {
        underTest = ConstructConstructionAreaProcess.builder()
            .processId(PROCESS_ID)
            .constructionId(CONSTRUCTION_ID)
            .status(ProcessStatus.CREATED)
            .location(LOCATION)
            .game(game)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.CONSTRUCT_CONSTRUCTION_AREA);
    }

    @Test
    void getExternalReference() {
        assertThat(underTest.getExternalReference()).isEqualTo(CONSTRUCTION_ID);
    }

    @Test
    void getPriority() {
        given(gameData.getPriorities()).willReturn(priorities);
        given(priorities.findByLocationAndType(LOCATION, PriorityType.CONSTRUCTION)).willReturn(priority);
        given(priority.getValue()).willReturn(BASE_PRIORITY);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByIdValidated(CONSTRUCTION_ID)).willReturn(construction);
        given(construction.getPriority()).willReturn(CONSTRUCTION_PRIORITY);
        given(game.getData()).willReturn(gameData);

        assertThat(underTest.getPriority()).isEqualTo(600);
    }

    @Test
    void work_waitingForResources() {
        given(applicationContextProxy.getBean(ConstructConstructionAreaProcessHelper.class)).willReturn(helper);
        given(applicationContextProxy.getBean(ConstructConstructionAreaProcessConditions.class)).willReturn(conditions);
        given(conditions.resourcesAvailable(gameData, PROCESS_ID, CONSTRUCTION_ID)).willReturn(false);
        given(game.getData()).willReturn(gameData);

        underTest.work();

        then(helper).should().createResourceRequestProcess(game, LOCATION, PROCESS_ID, CONSTRUCTION_ID);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
    }

    @Test
    void work_waitingForWork() {
        given(applicationContextProxy.getBean(ConstructConstructionAreaProcessHelper.class)).willReturn(helper);
        given(applicationContextProxy.getBean(ConstructConstructionAreaProcessConditions.class)).willReturn(conditions);
        given(conditions.resourcesAvailable(gameData, PROCESS_ID, CONSTRUCTION_ID)).willReturn(true);
        given(conditions.hasWorkProcesses(gameData, PROCESS_ID)).willReturn(false);
        given(conditions.workFinished(gameData, PROCESS_ID)).willReturn(false);
        given(game.getData()).willReturn(gameData);

        underTest.work();

        then(helper).should().createResourceRequestProcess(game, LOCATION, PROCESS_ID, CONSTRUCTION_ID);
        then(helper).should().startWork(game, PROCESS_ID, CONSTRUCTION_ID);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
    }

    @Test
    void work_finished() {
        given(applicationContextProxy.getBean(ConstructConstructionAreaProcessHelper.class)).willReturn(helper);
        given(applicationContextProxy.getBean(ConstructConstructionAreaProcessConditions.class)).willReturn(conditions);
        given(conditions.resourcesAvailable(gameData, PROCESS_ID, CONSTRUCTION_ID)).willReturn(true);
        given(conditions.hasWorkProcesses(gameData, PROCESS_ID)).willReturn(true);
        given(conditions.workFinished(gameData, PROCESS_ID)).willReturn(true);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(game.getData()).willReturn(gameData);

        underTest.work();

        then(helper).should().createResourceRequestProcess(game, LOCATION, PROCESS_ID, CONSTRUCTION_ID);
        then(helper).should().finishConstruction(progressDiff, gameData, CONSTRUCTION_ID);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);
    }

    @Test
    void cleanup() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(game.getData()).willReturn(gameData);

        underTest.cleanup();

        then(process).should().cleanup();
        then(progressDiff).should().save(underTest.toModel());

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);
    }

    @Test
    void toModel() {
        given(game.getGameId()).willReturn(GAME_ID);

        assertThat(underTest.toModel())
            .returns(PROCESS_ID, GameItem::getId)
            .returns(GameItemType.PROCESS, GameItem::getType)
            .returns(GAME_ID, GameItem::getGameId)
            .returns(ProcessType.CONSTRUCT_CONSTRUCTION_AREA, ProcessModel::getProcessType)
            .returns(ProcessStatus.CREATED, ProcessModel::getStatus)
            .returns(LOCATION, ProcessModel::getLocation)
            .returns(CONSTRUCTION_ID, ProcessModel::getExternalReference);
    }
}