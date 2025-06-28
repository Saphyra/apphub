package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class WorkProcessFactoryTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 1000;
    private static final Integer COMPLETED_WORK_POINTS = 500;
    private static final Integer MAX_WORK_POINTS_BATCH = 1500;

    @Mock
    private GameProperties gameProperties;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private WorkProcessFactory underTest;

    @Mock
    private Game game;

    @Mock
    private ProcessModel processModel;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private GameData gameData;

    @Mock
    private Processes processes;

    @Mock
    private GameProgressDiff progressDiff;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.WORK);
    }

    @Test
    void createFromModel() {
        given(processModel.getId()).willReturn(PROCESS_ID);
        given(processModel.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(processModel.getLocation()).willReturn(LOCATION);
        given(processModel.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);
        given(processModel.getData()).willReturn(Map.of(
            ProcessParamKeys.SKILL_TYPE, SkillType.MINING.name(),
            ProcessParamKeys.REQUIRED_WORK_POINTS, String.valueOf(REQUIRED_WORK_POINTS),
            ProcessParamKeys.COMPLETED_WORK_POINTS, String.valueOf(COMPLETED_WORK_POINTS)
        ));

        assertThat(underTest.createFromModel(game, processModel))
            .returns(PROCESS_ID, WorkProcess::getProcessId)
            .returns(EXTERNAL_REFERENCE, WorkProcess::getExternalReference)
            .returns(ProcessStatus.IN_PROGRESS, WorkProcess::getStatus)
            .returns(ProcessType.WORK, WorkProcess::getType);
    }

    @Test
    void save() {
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMaxWorkPointsBatch()).willReturn(MAX_WORK_POINTS_BATCH);
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);
        given(game.getData()).willReturn(gameData);
        given(gameData.getProcesses()).willReturn(processes);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.save(game, LOCATION, EXTERNAL_REFERENCE, REQUIRED_WORK_POINTS, SkillType.MINING);

        ArgumentCaptor<WorkProcess> processCaptor = ArgumentCaptor.forClass(WorkProcess.class);
        then(processes).should().add(processCaptor.capture());

        assertThat(processCaptor.getValue())
            .returns(PROCESS_ID, WorkProcess::getProcessId)
            .returns(EXTERNAL_REFERENCE, WorkProcess::getExternalReference)
            .returns(ProcessStatus.CREATED, WorkProcess::getStatus)
            .returns(ProcessType.WORK, WorkProcess::getType);

        then(progressDiff).should().save(processCaptor.getValue().toModel());
    }
}