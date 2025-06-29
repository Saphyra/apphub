package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.convoy_movement;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ConvoyMovementProcessFactoryTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final int REQUIRED_WORK_POINTS = 24;
    private static final int COMPLETED_WORK_POINTS = 3;
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String CITIZEN_ID_STRING = "citizen-id";
    private static final UUID CITIZEN_ID = UUID.randomUUID();

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ConvoyMovementProcessFactory underTest;

    @Mock
    private Game game;

    @Mock
    private ProcessModel model;

    @Mock
    private GameData gameData;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.CONVOY_MOVEMENT);
    }

    @Test
    void createFromModel() {
        given(model.getId()).willReturn(PROCESS_ID);
        given(model.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);
        given(model.getData()).willReturn(Map.of(
            ProcessParamKeys.CITIZEN_ID, CITIZEN_ID_STRING,
            ProcessParamKeys.REQUIRED_WORK_POINTS, String.valueOf(REQUIRED_WORK_POINTS),
            ProcessParamKeys.COMPLETED_WORK_POINTS, String.valueOf(COMPLETED_WORK_POINTS)
        ));
        given(model.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(model.getLocation()).willReturn(LOCATION);
        given(game.getData()).willReturn(gameData);
        given(uuidConverter.convertEntity(CITIZEN_ID_STRING)).willReturn(CITIZEN_ID);

        assertThat(underTest.createFromModel(game, model))
            .returns(PROCESS_ID, ConvoyMovementProcess::getProcessId)
            .returns(ProcessStatus.IN_PROGRESS, ConvoyMovementProcess::getStatus)
            .returns(EXTERNAL_REFERENCE, ConvoyMovementProcess::getExternalReference);
    }

    @Test
    void save() {
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);
        given(game.getData()).willReturn(gameData);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(game.getData().getProcesses()).willReturn(processes);
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);
        given(uuidConverter.convertDomain(CITIZEN_ID)).willReturn(CITIZEN_ID_STRING);

        ConvoyMovementProcess result = underTest.save(game, LOCATION, EXTERNAL_REFERENCE, CITIZEN_ID, REQUIRED_WORK_POINTS);

        assertThat(result)
            .returns(PROCESS_ID, ConvoyMovementProcess::getProcessId)
            .returns(ProcessStatus.CREATED, ConvoyMovementProcess::getStatus)
            .returns(EXTERNAL_REFERENCE, ConvoyMovementProcess::getExternalReference);

        then(progressDiff).should().save(result.toModel());
        then(processes).should().add(result);
    }
}