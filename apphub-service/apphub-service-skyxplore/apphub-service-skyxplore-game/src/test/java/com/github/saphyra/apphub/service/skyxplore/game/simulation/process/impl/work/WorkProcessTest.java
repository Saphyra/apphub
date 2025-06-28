package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WorkProcessTest {
    private static final int REQUIRED_WORK_POINTS = 5;
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final Integer PRIORITY = 234;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameData gameData;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private WorkProcessHelper helper;

    @Mock
    private WorkProcessConditions conditions;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private Game game;

    private WorkProcess underTest;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @BeforeEach
    void setUp() {
        underTest = WorkProcess.builder()
            .processId(PROCESS_ID)
            .status(ProcessStatus.CREATED)
            .externalReference(EXTERNAL_REFERENCE)
            .skillType(SkillType.AIMING)
            .requiredWorkPoints(REQUIRED_WORK_POINTS)
            .completedWorkPoints(0)
            .location(LOCATION)
            .applicationContextProxy(applicationContextProxy)
            .game(game)
            .build();
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
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.WORK);
    }

    @Test
    void work_cantProceed() {
        given(applicationContextProxy.getBean(WorkProcessHelper.class)).willReturn(helper);
        given(applicationContextProxy.getBean(WorkProcessConditions.class)).willReturn(conditions);
        given(game.getData()).willReturn(gameData);
        given(conditions.canProceed(gameData, EXTERNAL_REFERENCE)).willReturn(false);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.CREATED);
    }

    @Test
    void work_citizenNotAllocated() {
        given(applicationContextProxy.getBean(WorkProcessHelper.class)).willReturn(helper);
        given(applicationContextProxy.getBean(WorkProcessConditions.class)).willReturn(conditions);
        given(game.getData()).willReturn(gameData);
        given(conditions.canProceed(gameData, EXTERNAL_REFERENCE)).willReturn(true);
        given(helper.tryAllocateCitizen(progressDiff, gameData, LOCATION, PROCESS_ID, SkillType.AIMING)).willReturn(false);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.CREATED);
    }

    @Test
    void work_hasWorkLeft() {
        given(applicationContextProxy.getBean(WorkProcessHelper.class)).willReturn(helper);
        given(applicationContextProxy.getBean(WorkProcessConditions.class)).willReturn(conditions);
        given(game.getData()).willReturn(gameData);
        given(conditions.canProceed(gameData, EXTERNAL_REFERENCE)).willReturn(true);
        given(helper.tryAllocateCitizen(progressDiff, gameData, LOCATION, PROCESS_ID, SkillType.AIMING)).willReturn(true);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(helper.work(progressDiff, gameData, PROCESS_ID, SkillType.AIMING, REQUIRED_WORK_POINTS)).willReturn(REQUIRED_WORK_POINTS - 1);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
    }

    @Test
    void work_finished() {
        given(applicationContextProxy.getBean(WorkProcessHelper.class)).willReturn(helper);
        given(applicationContextProxy.getBean(WorkProcessConditions.class)).willReturn(conditions);
        given(game.getData()).willReturn(gameData);
        given(conditions.canProceed(gameData, EXTERNAL_REFERENCE)).willReturn(true);
        given(helper.tryAllocateCitizen(progressDiff, gameData, LOCATION, PROCESS_ID, SkillType.AIMING)).willReturn(true);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(helper.work(progressDiff, gameData, PROCESS_ID, SkillType.AIMING, REQUIRED_WORK_POINTS)).willReturn(REQUIRED_WORK_POINTS);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.DONE);

        then(helper).should().releaseCitizen(progressDiff, gameData, PROCESS_ID);
    }

    @Test
    void cleanup() {
        given(applicationContextProxy.getBean(WorkProcessHelper.class)).willReturn(helper);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.cleanup();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);

        verify(progressDiff).save(underTest.toModel());
    }

    @Test
    void toModel() {
        given(game.getGameId()).willReturn(GAME_ID);

        ProcessModel result = underTest.toModel();

        assertThat(result.getId()).isEqualTo(PROCESS_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.PROCESS);
        assertThat(result.getProcessType()).isEqualTo(ProcessType.WORK);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getData()).containsEntry(ProcessParamKeys.SKILL_TYPE, SkillType.AIMING.name());
        assertThat(result.getData()).containsEntry(ProcessParamKeys.REQUIRED_WORK_POINTS, String.valueOf(REQUIRED_WORK_POINTS));
        assertThat(result.getData()).containsEntry(ProcessParamKeys.COMPLETED_WORK_POINTS, String.valueOf(0));
    }
}