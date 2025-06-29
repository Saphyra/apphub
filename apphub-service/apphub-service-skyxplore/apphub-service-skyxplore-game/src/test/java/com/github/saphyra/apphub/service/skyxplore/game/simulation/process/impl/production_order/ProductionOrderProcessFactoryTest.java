package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

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
class ProductionOrderProcessFactoryTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final String PRODUCTION_ORDER_ID_STRING = "production-order-id";

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @InjectMocks
    private ProductionOrderProcessFactory underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Game game;

    @Mock
    private ProcessModel model;

    @Mock
    private Processes processes;

    @Mock
    private GameProgressDiff progressDiff;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.PRODUCTION_ORDER);
    }

    @Test
    void createFromModel() {
        given(model.getId()).willReturn(PROCESS_ID);
        given(model.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);
        given(model.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getData()).willReturn(Map.of(ProcessParamKeys.PRODUCTION_ORDER_ID, PRODUCTION_ORDER_ID_STRING));

        given(game.getData()).willReturn(gameData);
        given(uuidConverter.convertEntity(PRODUCTION_ORDER_ID_STRING)).willReturn(PRODUCTION_ORDER_ID);

        ProductionOrderProcess result = underTest.createFromModel(game, model);

        assertThat(result)
            .returns(PROCESS_ID, ProductionOrderProcess::getProcessId)
            .returns(ProcessStatus.IN_PROGRESS, ProductionOrderProcess::getStatus)
            .returns(EXTERNAL_REFERENCE, ProductionOrderProcess::getExternalReference)
            .returns(ProcessType.PRODUCTION_ORDER, ProductionOrderProcess::getType);
    }

    @Test
    void save() {
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);
        given(game.getData()).willReturn(gameData);
        given(gameData.getProcesses()).willReturn(processes);
        given(game.getProgressDiff()).willReturn(progressDiff);

        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);

        ProductionOrderProcess result = underTest.save(game, LOCATION, EXTERNAL_REFERENCE, PRODUCTION_ORDER_ID);

        assertThat(result)
            .returns(PROCESS_ID, ProductionOrderProcess::getProcessId)
            .returns(ProcessStatus.CREATED, ProductionOrderProcess::getStatus)
            .returns(EXTERNAL_REFERENCE, ProductionOrderProcess::getExternalReference)
            .returns(ProcessType.PRODUCTION_ORDER, ProductionOrderProcess::getType);

        then(processes).should().add(result);
        then(progressDiff).should().save(result.toModel());
    }
}