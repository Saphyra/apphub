package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_dispatcher;

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
class ProductionDispatcherProcessFactoryTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID PRODUCTION_REQUEST_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final String PRODUCTION_REQUEST_ID_STRING = "production-request-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @InjectMocks
    private ProductionDispatcherProcessFactory underTest;

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
        assertThat(underTest.getType()).isEqualTo(ProcessType.PRODUCTION_DISPATCHER);
    }

    @Test
    void createFromModel() {
        given(processModel.getId()).willReturn(PROCESS_ID);
        given(processModel.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);
        given(processModel.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(processModel.getData()).willReturn(Map.of(ProcessParamKeys.PRODUCTION_REQUEST_ID, PRODUCTION_REQUEST_ID_STRING));
        given(uuidConverter.convertEntity(PRODUCTION_REQUEST_ID_STRING)).willReturn(PRODUCTION_REQUEST_ID);
        given(processModel.getLocation()).willReturn(LOCATION);

        ProductionDispatcherProcess result = underTest.createFromModel(game, processModel);

        assertThat(result)
            .returns(PROCESS_ID, ProductionDispatcherProcess::getProcessId)
            .returns(ProcessType.PRODUCTION_DISPATCHER, ProductionDispatcherProcess::getType)
            .returns(ProcessStatus.IN_PROGRESS, ProductionDispatcherProcess::getStatus)
            .returns(EXTERNAL_REFERENCE, ProductionDispatcherProcess::getExternalReference);
    }

    @Test
    void save() {
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);
        given(game.getData()).willReturn(gameData);
        given(gameData.getProcesses()).willReturn(processes);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);

        ProductionDispatcherProcess result = underTest.save(game, LOCATION, EXTERNAL_REFERENCE, PRODUCTION_REQUEST_ID);

        then(processes).should().add(result);
        then(progressDiff).should().save(result.toModel());

        assertThat(result)
            .returns(PROCESS_ID, ProductionDispatcherProcess::getProcessId)
            .returns(ProcessType.PRODUCTION_DISPATCHER, ProductionDispatcherProcess::getType)
            .returns(ProcessStatus.CREATED, ProductionDispatcherProcess::getStatus)
            .returns(EXTERNAL_REFERENCE, ProductionDispatcherProcess::getExternalReference);
    }
}