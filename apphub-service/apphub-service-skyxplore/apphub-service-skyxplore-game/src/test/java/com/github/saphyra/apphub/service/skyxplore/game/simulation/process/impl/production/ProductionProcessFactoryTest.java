package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production;

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
class ProductionProcessFactoryTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final int AMOUNT = 23;
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final String PRODUCTION_ORDER_ID_STRING = "production-order-id";
    private static final String BUILDING_MODULE_ID_STRING = "building-module-id";

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @InjectMocks
    private ProductionProcessFactory underTest;

    @Mock
    private ProcessModel model;

    @Mock
    private Game game;

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
        assertThat(underTest.getType()).isEqualTo(ProcessType.PRODUCTION);
    }

    @Test
    void createFromModel() {
        given(model.getId()).willReturn(PROCESS_ID);
        given(model.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(model.getData()).willReturn(Map.of(
            ProcessParamKeys.PRODUCTION_ORDER_ID, PRODUCTION_ORDER_ID_STRING,
            ProcessParamKeys.BUILDING_MODULE_ID, BUILDING_MODULE_ID_STRING,
            ProcessParamKeys.AMOUNT, String.valueOf(AMOUNT)
        ));

        given(uuidConverter.convertEntity(PRODUCTION_ORDER_ID_STRING)).willReturn(PRODUCTION_ORDER_ID);
        given(uuidConverter.convertEntity(BUILDING_MODULE_ID_STRING)).willReturn(BUILDING_MODULE_ID);

        ProductionProcess result = underTest.createFromModel(game, model);

        assertThat(result)
            .returns(PROCESS_ID, Process::getProcessId)
            .returns(ProcessStatus.IN_PROGRESS, Process::getStatus)
            .returns(EXTERNAL_REFERENCE, Process::getExternalReference)
            .returns(ProcessType.PRODUCTION, Process::getType);
    }

    @Test
    void save() {
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);
        given(game.getData()).willReturn(gameData);
        given(gameData.getProcesses()).willReturn(processes);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);

        ProductionProcess result = underTest.save(game, LOCATION, EXTERNAL_REFERENCE, PRODUCTION_ORDER_ID, BUILDING_MODULE_ID, AMOUNT);

        assertThat(result)
            .returns(PROCESS_ID, Process::getProcessId)
            .returns(ProcessStatus.CREATED, Process::getStatus)
            .returns(EXTERNAL_REFERENCE, Process::getExternalReference)
            .returns(ProcessType.PRODUCTION, Process::getType);

        then(processes).should().add(result);
        then(progressDiff).should().save(result.toModel());
    }
}