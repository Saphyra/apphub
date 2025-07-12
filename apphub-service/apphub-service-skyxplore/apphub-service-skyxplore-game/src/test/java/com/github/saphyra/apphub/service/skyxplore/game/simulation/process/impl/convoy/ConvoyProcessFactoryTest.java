package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.convoy;

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
class ConvoyProcessFactoryTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final String CONVOY_ID_STRING = "convoy-id";
    private static final UUID CONVOY_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @InjectMocks
    private ConvoyProcessFactory underTest;

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

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.CONVOY);
    }

    @Test
    void createFromModel() {
        given(model.getId()).willReturn(PROCESS_ID);
        given(model.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);
        given(model.getData()).willReturn(Map.of(ProcessParamKeys.CONVOY_ID, CONVOY_ID_STRING));
        given(model.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(model.getLocation()).willReturn(LOCATION);

        given(uuidConverter.convertEntity(CONVOY_ID_STRING)).willReturn(CONVOY_ID);

        assertThat(underTest.createFromModel(game, model))
            .returns(PROCESS_ID, ConvoyProcess::getProcessId)
            .returns(ProcessStatus.IN_PROGRESS, ConvoyProcess::getStatus)
            .returns(EXTERNAL_REFERENCE, ConvoyProcess::getExternalReference)
            .returns(ProcessType.CONVOY, ConvoyProcess::getType);
    }

    @Test
    void save() {
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);
        given(game.getData()).willReturn(gameData);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(gameData.getProcesses()).willReturn(processes);
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);

        ConvoyProcess result = underTest.save(game, LOCATION, EXTERNAL_REFERENCE, CONVOY_ID);

        assertThat(result)
            .returns(PROCESS_ID, ConvoyProcess::getProcessId)
            .returns(ProcessStatus.CREATED, ConvoyProcess::getStatus)
            .returns(EXTERNAL_REFERENCE, ConvoyProcess::getExternalReference)
            .returns(ProcessType.CONVOY, ConvoyProcess::getType);

        then(processes).should().add(result);
        then(progressDiff).should().save(result.toModel());
    }
}