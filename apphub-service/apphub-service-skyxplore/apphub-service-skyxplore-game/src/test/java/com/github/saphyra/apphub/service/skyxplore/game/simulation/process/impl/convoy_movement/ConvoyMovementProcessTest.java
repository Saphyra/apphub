package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.convoy_movement;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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

@ExtendWith(MockitoExtension.class)
class ConvoyMovementProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final int REQUIRED_WORK_POINTS = 10;
    private static final int COMPLETED_WORK_POINTS = 3;
    private static final int PRIORITY = 2;
    private static final String CITIZEN_ID_STRING = "citizen-id";

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private ConvoyMovementProcessHelper helper;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private GameProgressDiff progressDiff;

    private ConvoyMovementProcess underTest;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @BeforeEach
    void setUp() {
        underTest = ConvoyMovementProcess.builder()
            .processId(PROCESS_ID)
            .status(ProcessStatus.CREATED)
            .citizenId(CITIZEN_ID)
            .requiredWorkPoints(REQUIRED_WORK_POINTS)
            .completedWorkPoints(COMPLETED_WORK_POINTS)
            .externalReference(EXTERNAL_REFERENCE)
            .gameData(gameData)
            .location(LOCATION)
            .applicationContextProxy(applicationContextProxy)
            .game(game)
            .build();
    }

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.CONVOY_MOVEMENT);
    }

    @Test
    void getPriority() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.findByIdValidated(EXTERNAL_REFERENCE)).willReturn(process);
        given(process.getPriority()).willReturn(PRIORITY);

        assertThat(underTest.getPriority()).isEqualTo(PRIORITY + 1);
    }

    @Test
    void work_workInProgress() {
        given(applicationContextProxy.getBean(ConvoyMovementProcessHelper.class)).willReturn(helper);
        given(helper.getWorkPointsPerTick(gameData, CITIZEN_ID, REQUIRED_WORK_POINTS - COMPLETED_WORK_POINTS)).willReturn(4);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);

        then(helper).should().work(progressDiff, gameData, CITIZEN_ID, SkillType.LOGISTICS, 4);
    }

    @Test
    void work_finished() {
        given(applicationContextProxy.getBean(ConvoyMovementProcessHelper.class)).willReturn(helper);
        given(helper.getWorkPointsPerTick(gameData, CITIZEN_ID, REQUIRED_WORK_POINTS - COMPLETED_WORK_POINTS)).willReturn(7);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.DONE);

        then(helper).should().work(progressDiff, gameData, CITIZEN_ID, SkillType.LOGISTICS, 7);
    }

    @Test
    void cleanup() {
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);
        given(uuidConverter.convertDomain(CITIZEN_ID)).willReturn(CITIZEN_ID_STRING);

        underTest.cleanup();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);
        then(progressDiff).should().save(underTest.toModel());
    }

    @Test
    void toModel() {
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);
        given(uuidConverter.convertDomain(CITIZEN_ID)).willReturn(CITIZEN_ID_STRING);

        var model = underTest.toModel();

        assertThat(model.getId()).isEqualTo(PROCESS_ID);
        assertThat(model.getGameId()).isEqualTo(gameData.getGameId());
        assertThat(model.getType()).isEqualTo(GameItemType.PROCESS);
        assertThat(model.getProcessType()).isEqualTo(ProcessType.CONVOY_MOVEMENT);
        assertThat(model.getStatus()).isEqualTo(ProcessStatus.CREATED);
        assertThat(model.getLocation()).isEqualTo(LOCATION);
        assertThat(model.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(model.getData().get(ProcessParamKeys.CITIZEN_ID)).isEqualTo(CITIZEN_ID_STRING);
        assertThat(model.getData().get(ProcessParamKeys.REQUIRED_WORK_POINTS)).isEqualTo(String.valueOf(REQUIRED_WORK_POINTS));
        assertThat(model.getData().get(ProcessParamKeys.COMPLETED_WORK_POINTS)).isEqualTo(String.valueOf(COMPLETED_WORK_POINTS));
    }
}