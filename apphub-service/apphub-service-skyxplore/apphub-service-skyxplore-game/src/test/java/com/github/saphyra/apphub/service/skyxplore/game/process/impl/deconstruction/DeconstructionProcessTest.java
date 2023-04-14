package com.github.saphyra.apphub.service.skyxplore.game.process.impl.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priorities;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priority;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeconstructionProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final int BASE_PRIORITY = 425;
    private static final Integer DECONSTRUCTION_PRIORITY = 452;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private RequestWorkProcessFactoryForDeconstruction requestWorkProcessFactoryForDeconstruction;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private FinishDeconstructionService finishDeconstructionService;

    private DeconstructionProcess underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private SyncCache syncCache;

    @Mock
    private RequestWorkProcess requestWorkProcess;

    @Mock
    private Processes processes;

    @Mock
    private ProcessModel requestWorkProcessModel;

    @Mock
    private Priorities priorities;

    @Mock
    private Priority priority;

    @BeforeEach
    public void setUp() {
        setUp(ProcessStatus.CREATED);
    }

    public void setUp(ProcessStatus status) {
        underTest = DeconstructionProcess.builder()
            .processId(PROCESS_ID)
            .status(status)
            .deconstruction(deconstruction)
            .gameData(gameData)
            .location(LOCATION)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Test
    void getExternalReference() {
        given(deconstruction.getDeconstructionId()).willReturn(DECONSTRUCTION_ID);

        UUID result = underTest.getExternalReference();

        assertThat(result).isEqualTo(DECONSTRUCTION_ID);
    }

    @Test
    public void getPriority() {
        given(gameData.getPriorities()).willReturn(priorities);
        given(priorities.findByLocationAndType(LOCATION, PriorityType.CONSTRUCTION)).willReturn(priority);
        given(priority.getValue()).willReturn(BASE_PRIORITY);
        given(deconstruction.getPriority()).willReturn(DECONSTRUCTION_PRIORITY);

        int result = underTest.getPriority();

        assertThat(result).isEqualTo(BASE_PRIORITY * DECONSTRUCTION_PRIORITY * GameConstants.PROCESS_PRIORITY_MULTIPLIER);
    }

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.DECONSTRUCTION);
    }

    @Test
    void work_createdStatus() {
        given(deconstruction.getDeconstructionId()).willReturn(DECONSTRUCTION_ID);
        given(gameData.getProcesses()).willReturn(processes);
        given(requestWorkProcess.toModel()).willReturn(requestWorkProcessModel);
        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.REQUEST_WORK)).willReturn(List.of(requestWorkProcess));
        given(requestWorkProcess.getStatus()).willReturn(ProcessStatus.CREATED);

        given(applicationContextProxy.getBean(RequestWorkProcessFactoryForDeconstruction.class)).willReturn(requestWorkProcessFactoryForDeconstruction);

        given(requestWorkProcessFactoryForDeconstruction.createRequestWorkProcesses(gameData, LOCATION, PROCESS_ID, DECONSTRUCTION_ID)).willReturn(List.of(requestWorkProcess));

        underTest.work(syncCache);

        //noinspection RedundantCollectionOperation
        verify(processes).addAll(List.of(requestWorkProcess));
        verify(syncCache).saveGameItem(requestWorkProcessModel);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
    }

    @Test
    void work_finish() {
        setUp(ProcessStatus.IN_PROGRESS);

        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.REQUEST_WORK)).willReturn(List.of(requestWorkProcess));
        given(requestWorkProcess.getStatus()).willReturn(ProcessStatus.DONE);

        given(applicationContextProxy.getBean(FinishDeconstructionService.class)).willReturn(finishDeconstructionService);

        underTest.work(syncCache);

        verify(finishDeconstructionService).finishDeconstruction(gameData, LOCATION, syncCache, deconstruction);
        verify(requestWorkProcess).cleanup(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);
    }

    @Test
    void cancel() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(requestWorkProcess));

        underTest.cancel(syncCache);

        verify(requestWorkProcess).cancel(syncCache);
        verify(syncCache).saveGameItem(underTest.toModel());

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);
    }

    @Test
    void toModel() {
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(deconstruction.getDeconstructionId()).willReturn(DECONSTRUCTION_ID);

        ProcessModel result = underTest.toModel();

        assertThat(result.getId()).isEqualTo(PROCESS_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.PROCESS);
        assertThat(result.getProcessType()).isEqualTo(ProcessType.DECONSTRUCTION);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getExternalReference()).isEqualTo(DECONSTRUCTION_ID);
    }
}