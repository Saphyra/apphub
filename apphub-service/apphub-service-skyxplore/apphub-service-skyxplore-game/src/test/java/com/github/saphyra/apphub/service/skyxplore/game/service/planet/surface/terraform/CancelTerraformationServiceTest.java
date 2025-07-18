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
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Slf4j
@MockitoSettings(strictness = Strictness.LENIENT)
public class CancelTerraformationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

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

    @Mock
    private Planets planets;

    @Mock
    private Planet planet;

    @BeforeEach
    void setUp() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(gameData.getPlanets()).willReturn(planets);
        given(terraformation.getLocation()).willReturn(PLANET_ID);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(gameData.getProcesses()).willReturn(processes);
        given(terraformation.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(processes.findByExternalReferenceAndTypeValidated(CONSTRUCTION_ID, ProcessType.TERRAFORMATION)).willReturn(process);
    }

    @Test
    void cancelTerraformationQueueItem_forbiddenOperation() {
        given(constructions.findByIdValidated(CONSTRUCTION_ID)).willReturn(terraformation);
        given(planet.getOwner()).willReturn(UUID.randomUUID());

        ExceptionValidator.validateForbiddenOperation(() -> underTest.cancelTerraformationQueueItem(USER_ID, CONSTRUCTION_ID));
    }

    @Test
    public void cancelTerraformationQueueItem() {
        given(constructions.findByIdValidated(CONSTRUCTION_ID)).willReturn(terraformation);
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
    void cancelTerraformationOfSurface_forbiddenOperation() {
        given(constructions.findByExternalReferenceValidated(SURFACE_ID)).willReturn(terraformation);
        given(planet.getOwner()).willReturn(UUID.randomUUID());

        ExceptionValidator.validateForbiddenOperation(() -> underTest.cancelTerraformationOfSurface(USER_ID, SURFACE_ID));
    }

    @Test
    public void cancelTerraformationOfSurface() {
        given(constructions.findByExternalReferenceValidated(SURFACE_ID)).willReturn(terraformation);
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