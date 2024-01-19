package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class CancelTerraformationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private AllocationRemovalService allocationRemovalService;

    @InjectMocks
    private CancelTerraformationService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Construction terraformation;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Mock
    private ExecutionResult<Void> executionResult;

    @Mock
    private Constructions constructions;

    @Test
    public void cancelTerraformationQueueItem() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByConstructionIdValidated(CONSTRUCTION_ID)).willReturn(terraformation);

        //Common
        given(game.getEventLoop()).willReturn(eventLoop);
        given(gameData.getProcesses()).willReturn(processes);
        given(terraformation.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(processes.findByExternalReferenceAndTypeValidated(CONSTRUCTION_ID, ProcessType.TERRAFORMATION)).willReturn(process);

        given(eventLoop.processWithWait(any(Runnable.class))).willReturn(executionResult);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.cancelTerraformationQueueItem(USER_ID, CONSTRUCTION_ID);

        //Common
        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).processWithWait(argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();

        verify(process).cleanup();
        verify(constructions).remove(terraformation);
        verify(allocationRemovalService).removeAllocationsAndReservations(progressDiff, gameData, CONSTRUCTION_ID);
        verify(executionResult).getOrThrow();
        then(progressDiff).should().delete(CONSTRUCTION_ID, GameItemType.CONSTRUCTION);
    }

    @Test
    public void cancelTerraformationOfSurface() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReferenceValidated(SURFACE_ID)).willReturn(terraformation);

        //Common
        given(game.getEventLoop()).willReturn(eventLoop);
        given(gameData.getProcesses()).willReturn(processes);
        given(terraformation.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(processes.findByExternalReferenceAndTypeValidated(CONSTRUCTION_ID, ProcessType.TERRAFORMATION)).willReturn(process);

        given(eventLoop.processWithWait(any(Runnable.class))).willReturn(executionResult);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.cancelTerraformationOfSurface(USER_ID, SURFACE_ID);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).processWithWait(argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();

        verify(process).cleanup();
        verify(constructions).remove(terraformation);
        verify(allocationRemovalService).removeAllocationsAndReservations(progressDiff, gameData, CONSTRUCTION_ID);
        verify(executionResult).getOrThrow();
        then(progressDiff).should().delete(CONSTRUCTION_ID, GameItemType.CONSTRUCTION);
    }
}