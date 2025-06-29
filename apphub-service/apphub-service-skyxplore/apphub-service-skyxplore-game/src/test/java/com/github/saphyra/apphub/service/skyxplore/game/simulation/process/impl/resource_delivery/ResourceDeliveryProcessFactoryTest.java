package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_delivery;

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
class ResourceDeliveryProcessFactoryTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID RESOURCE_DELIVERY_REQUEST_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final String RESOURCE_DELIVERY_REQUEST_ID_STRING = "resource-delivery-request-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ResourceDeliveryProcessFactory underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private Processes processes;

    @Mock
    private ProcessModel model;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.RESOURCE_DELIVERY);
    }

    @Test
    void createFromModel() {
        given(model.getId()).willReturn(PROCESS_ID);
        given(model.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);
        given(model.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getData()).willReturn(Map.of(ProcessParamKeys.RESOURCE_DELIVERY_REQUEST_ID, RESOURCE_DELIVERY_REQUEST_ID_STRING));
        given(uuidConverter.convertEntity(RESOURCE_DELIVERY_REQUEST_ID_STRING)).willReturn(RESOURCE_DELIVERY_REQUEST_ID);
        given(game.getData()).willReturn(gameData);

        ResourceDeliveryProcess result = underTest.createFromModel(game, model);

        assertThat(result)
            .returns(PROCESS_ID, ResourceDeliveryProcess::getProcessId)
            .returns(ProcessType.RESOURCE_DELIVERY, ResourceDeliveryProcess::getType)
            .returns(EXTERNAL_REFERENCE, ResourceDeliveryProcess::getExternalReference)
            .returns(ProcessStatus.IN_PROGRESS, ResourceDeliveryProcess::getStatus);
    }

    @Test
    void save() {
        given(game.getData()).willReturn(gameData);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(gameData.getProcesses()).willReturn(processes);
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);

        ResourceDeliveryProcess result = underTest.save(game, LOCATION, EXTERNAL_REFERENCE, RESOURCE_DELIVERY_REQUEST_ID);

        assertThat(result)
            .returns(PROCESS_ID, ResourceDeliveryProcess::getProcessId)
            .returns(ProcessType.RESOURCE_DELIVERY, ResourceDeliveryProcess::getType)
            .returns(EXTERNAL_REFERENCE, ResourceDeliveryProcess::getExternalReference)
            .returns(ProcessStatus.CREATED, ResourceDeliveryProcess::getStatus);

        then(progressDiff).should().save(result.toModel());
        then(processes).should().add(result);
    }
}