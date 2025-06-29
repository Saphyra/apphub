package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_dispatcher;

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
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request.ProductionRequest;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request.ProductionRequests;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProductionDispatcherProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID PRODUCTION_REQUEST_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final int PRIORITY = 1;
    private static final String PRODUCTION_REQUEST_ID_STRING = "production-request-id";
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private Game game;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private GameData gameData;

    @Mock
    private ProductionDispatcherProcessHelper helper;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private UuidConverter uuidConverter;

    private ProductionDispatcherProcess underTest;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Mock
    private ProductionRequests productionRequests;

    @Mock
    private ProductionRequest productionRequest;

    @BeforeEach
    void setUp() {
        underTest = ProductionDispatcherProcess.builder()
            .processId(PROCESS_ID)
            .status(ProcessStatus.CREATED)
            .productionRequestId(PRODUCTION_REQUEST_ID)
            .externalReference(EXTERNAL_REFERENCE)
            .location(LOCATION)
            .applicationContextProxy(applicationContextProxy)
            .game(game)
            .build();
    }

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.PRODUCTION_DISPATCHER);
    }

    @Test
    void getPriority() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.findByIdValidated(EXTERNAL_REFERENCE)).willReturn(process);
        given(process.getPriority()).willReturn(PRIORITY);

        assertThat(underTest.getPriority()).isEqualTo(PRIORITY + 1);
    }

    @Test
    void work_amountMissing() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getProductionRequests()).willReturn(productionRequests);
        given(productionRequests.findByIdValidated(PRODUCTION_REQUEST_ID)).willReturn(productionRequest);
        given(productionRequest.getDispatchedAmount()).willReturn(3);
        given(productionRequest.getRequestedAmount()).willReturn(10);
        given(applicationContextProxy.getBean(ProductionDispatcherProcessHelper.class)).willReturn(helper);
        given(helper.dispatch(game, LOCATION, PROCESS_ID, PRODUCTION_REQUEST_ID, 7)).willReturn(6);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
    }

    @Test
    void work_waitingForChildren() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getProductionRequests()).willReturn(productionRequests);
        given(productionRequests.findByIdValidated(PRODUCTION_REQUEST_ID)).willReturn(productionRequest);
        given(productionRequest.getDispatchedAmount()).willReturn(3);
        given(productionRequest.getRequestedAmount()).willReturn(10);
        given(applicationContextProxy.getBean(ProductionDispatcherProcessHelper.class)).willReturn(helper);
        given(helper.dispatch(game, LOCATION, PROCESS_ID, PRODUCTION_REQUEST_ID, 7)).willReturn(7);
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(process.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
    }

    @Test
    void work_finished() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getProductionRequests()).willReturn(productionRequests);
        given(productionRequests.findByIdValidated(PRODUCTION_REQUEST_ID)).willReturn(productionRequest);
        given(productionRequest.getDispatchedAmount()).willReturn(10);
        given(productionRequest.getRequestedAmount()).willReturn(10);
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(process.getStatus()).willReturn(ProcessStatus.DONE);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.DONE);
    }

    @Test
    void cleanup() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(gameData.getProductionRequests()).willReturn(productionRequests);
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);

        underTest.cleanup();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);

        then(process).should().cleanup();
        then(productionRequests).should().remove(PRODUCTION_REQUEST_ID);
        then(progressDiff).should().delete(PRODUCTION_REQUEST_ID, GameItemType.PRODUCTION_REQUEST);
        then(progressDiff).should().save(underTest.toModel());
    }

    @Test
    void toModel() {
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);
        given(game.getGameId()).willReturn(GAME_ID);

        given(uuidConverter.convertDomain(PRODUCTION_REQUEST_ID)).willReturn(PRODUCTION_REQUEST_ID_STRING);


        assertThat(underTest.toModel())
            .returns(PROCESS_ID, GameItem::getId)
            .returns(GAME_ID, GameItem::getGameId)
            .returns(GameItemType.PROCESS, GameItem::getType)
            .returns(ProcessType.PRODUCTION_DISPATCHER, ProcessModel::getProcessType)
            .returns(ProcessStatus.CREATED, ProcessModel::getStatus)
            .returns(LOCATION, ProcessModel::getLocation)
            .returns(EXTERNAL_REFERENCE, ProcessModel::getExternalReference)
            .returns(PRODUCTION_REQUEST_ID_STRING, processModel -> processModel.getData().get(ProcessParamKeys.PRODUCTION_REQUEST_ID));

    }
}