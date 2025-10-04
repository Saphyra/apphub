package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_delivery;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ResourceDeliveryProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID RESOURCE_DELIVERY_REQUEST_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final int PRIORITY = 10;
    private static final Integer TO_DELVER = 100;
    private static final String RESOURCE_DELIVERY_REQUEST_ID_STRING = "resource_delivery_request_id";

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private ResourceDeliveryProcessHelper helper;

    @Mock
    private AllocationRemovalService allocationRemovalService;

    @Mock
    private UuidConverter uuidConverter;

    private ResourceDeliveryProcess underTest;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @BeforeEach
    void setUp() {
        underTest = ResourceDeliveryProcess.builder()
            .processId(PROCESS_ID)
            .status(ProcessStatus.CREATED)
            .resourceDeliveryRequestId(RESOURCE_DELIVERY_REQUEST_ID)
            .externalReference(EXTERNAL_REFERENCE)
            .location(LOCATION)
            .applicationContextProxy(applicationContextProxy)
            .game(game)
            .build();
    }

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.RESOURCE_DELIVERY);
    }

    @Test
    void getPriority() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.findByIdValidated(EXTERNAL_REFERENCE)).willReturn(process);
        given(process.getPriority()).willReturn(PRIORITY);
        given(game.getData()).willReturn(gameData);

        assertThat(underTest.getPriority()).isEqualTo(PRIORITY + 1);
    }

    @Test
    void work_noConvoy() {
        given(applicationContextProxy.getBean(ResourceDeliveryProcessHelper.class)).willReturn(helper);
        given(helper.calculateToDeliver(gameData, RESOURCE_DELIVERY_REQUEST_ID)).willReturn(TO_DELVER);
        given(helper.assembleConvoy(game, LOCATION, PROCESS_ID, RESOURCE_DELIVERY_REQUEST_ID, TO_DELVER)).willReturn(Optional.empty());
        given(game.getData()).willReturn(gameData);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
    }

    @Test
    void work_allDeliveryInitiated() {
        given(applicationContextProxy.getBean(ResourceDeliveryProcessHelper.class)).willReturn(helper);
        given(helper.calculateToDeliver(gameData, RESOURCE_DELIVERY_REQUEST_ID)).willReturn(TO_DELVER);
        given(helper.assembleConvoy(game, LOCATION, PROCESS_ID, RESOURCE_DELIVERY_REQUEST_ID, TO_DELVER)).willReturn(Optional.of(TO_DELVER));
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(process.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);
        given(game.getData()).willReturn(gameData);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
    }

    @Test
    void work_done() {
        given(applicationContextProxy.getBean(ResourceDeliveryProcessHelper.class)).willReturn(helper);
        given(helper.calculateToDeliver(gameData, RESOURCE_DELIVERY_REQUEST_ID)).willReturn(TO_DELVER);
        given(helper.assembleConvoy(game, LOCATION, PROCESS_ID, RESOURCE_DELIVERY_REQUEST_ID, TO_DELVER)).willReturn(Optional.of(TO_DELVER));
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(process.getStatus()).willReturn(ProcessStatus.DONE);
        given(game.getData()).willReturn(gameData);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.DONE);
    }

    @Test
    void cleanup() {
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(applicationContextProxy.getBean(AllocationRemovalService.class)).willReturn(allocationRemovalService);
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);
        given(game.getData()).willReturn(gameData);

        underTest.cleanup();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);

        then(allocationRemovalService).should().removeAllocationsAndReservations(progressDiff, gameData, RESOURCE_DELIVERY_REQUEST_ID);
        then(processes).should().getByExternalReference(PROCESS_ID);
        then(process).should().cleanup();
        then(progressDiff).should().save(underTest.toModel());
    }

    @Test
    void toModel() {
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);
        given(game.getGameId()).willReturn(GAME_ID);
        given(uuidConverter.convertDomain(RESOURCE_DELIVERY_REQUEST_ID)).willReturn(RESOURCE_DELIVERY_REQUEST_ID_STRING);

        assertThat(underTest.toModel())
            .returns(PROCESS_ID, GameItem::getId)
            .returns(GameItemType.PROCESS, GameItem::getType)
            .returns(GAME_ID, GameItem::getGameId)
            .returns(ProcessType.RESOURCE_DELIVERY, ProcessModel::getProcessType)
            .returns(ProcessStatus.CREATED, ProcessModel::getStatus)
            .returns(LOCATION, ProcessModel::getLocation)
            .returns(EXTERNAL_REFERENCE, ProcessModel::getExternalReference)
            .returns(RESOURCE_DELIVERY_REQUEST_ID_STRING, processModel -> processModel.getData().get(ProcessParamKeys.RESOURCE_DELIVERY_REQUEST_ID));
    }
}