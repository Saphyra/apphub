package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_request;

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
class ResourceRequestProcessFactoryTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final String RESERVED_STORAGE_ID_STRING = "reserved-storage-id";

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ResourceRequestProcessFactory underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private Processes processes;

    @Mock
    private ProcessModel processModel;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.RESOURCE_REQUEST);
    }

    @Test
    void createFromModel() {
        given(processModel.getId()).willReturn(PROCESS_ID);
        given(processModel.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);
        given(processModel.getLocation()).willReturn(LOCATION);
        given(processModel.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(processModel.getData()).willReturn(Map.of(ProcessParamKeys.RESERVED_STORAGE_ID, RESERVED_STORAGE_ID_STRING));
        given(uuidConverter.convertEntity(RESERVED_STORAGE_ID_STRING)).willReturn(RESERVED_STORAGE_ID);

        ResourceRequestProcess result = underTest.createFromModel(game, processModel);

        assertThat(result)
            .returns(PROCESS_ID, ResourceRequestProcess::getProcessId)
            .returns(ProcessType.RESOURCE_REQUEST, ResourceRequestProcess::getType)
            .returns(ProcessStatus.IN_PROGRESS, ResourceRequestProcess::getStatus)
            .returns(EXTERNAL_REFERENCE, ResourceRequestProcess::getExternalReference);
    }

    @Test
    void save() {
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);
        given(game.getData()).willReturn(gameData);
        given(gameData.getProcesses()).willReturn(processes);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);

        ResourceRequestProcess result = underTest.save(game, LOCATION, EXTERNAL_REFERENCE, RESERVED_STORAGE_ID);

        assertThat(result)
            .returns(PROCESS_ID, ResourceRequestProcess::getProcessId)
            .returns(ProcessType.RESOURCE_REQUEST, ResourceRequestProcess::getType)
            .returns(ProcessStatus.CREATED, ResourceRequestProcess::getStatus)
            .returns(EXTERNAL_REFERENCE, ResourceRequestProcess::getExternalReference);

        then(progressDiff).should().save(result.toModel());
        then(processes).should().add(result);
    }
}