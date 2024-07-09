package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
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

@ExtendWith(MockitoExtension.class)
class CancelDeconstructionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @InjectMocks
    private CancelDeconstructionService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Deconstruction deconstruction;

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
    private Deconstructions deconstructions;

    @Mock
    private Planets planets;

    @Mock
    private Planet planet;

    @Test
    void cancelDeconstructionOfDeconstruction() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByDeconstructionIdValidated(DECONSTRUCTION_ID)).willReturn(deconstruction);

        given(game.getEventLoop()).willReturn(eventLoop);
        given(deconstruction.getDeconstructionId()).willReturn(DECONSTRUCTION_ID);

        given(gameData.getProcesses()).willReturn(processes);
        given(processes.findByExternalReferenceAndTypeValidated(DECONSTRUCTION_ID, ProcessType.DECONSTRUCTION)).willReturn(process);
        given(eventLoop.processWithWait(any(Runnable.class))).willReturn(executionResult);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.cancelDeconstructionOfDeconstruction(USER_ID, DECONSTRUCTION_ID);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        then(eventLoop).should().processWithWait(argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();

        then(deconstructions).should().remove(deconstruction);
        then(process).should().cleanup();
        then(executionResult).should().getOrThrow();
        then(progressDiff).should().delete(DECONSTRUCTION_ID, GameItemType.DECONSTRUCTION);
    }

    @Test
    void cancelDeconstructionOfBuilding_forbiddenOperation() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReferenceValidated(BUILDING_ID)).willReturn(deconstruction);
        given(gameData.getPlanets()).willReturn(planets);
        given(deconstruction.getLocation()).willReturn(PLANET_ID);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(UUID.randomUUID());

        ExceptionValidator.validateForbiddenOperation(() -> underTest.cancelDeconstructionOfBuilding(USER_ID, BUILDING_ID));
    }

    @Test
    void cancelDeconstructionOfBuilding() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReferenceValidated(BUILDING_ID)).willReturn(deconstruction);
        given(gameData.getPlanets()).willReturn(planets);
        given(deconstruction.getLocation()).willReturn(PLANET_ID);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);

        given(game.getEventLoop()).willReturn(eventLoop);
        given(deconstruction.getDeconstructionId()).willReturn(DECONSTRUCTION_ID);

        given(gameData.getProcesses()).willReturn(processes);
        given(processes.findByExternalReferenceAndTypeValidated(DECONSTRUCTION_ID, ProcessType.DECONSTRUCTION)).willReturn(process);
        given(eventLoop.processWithWait(any(Runnable.class))).willReturn(executionResult);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.cancelDeconstructionOfBuilding(USER_ID, BUILDING_ID);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        then(eventLoop).should().processWithWait(argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();

        then(deconstructions).should().remove(deconstruction);
        then(process).should().cleanup();
        then(executionResult).should().getOrThrow();

        then(progressDiff).should().delete(DECONSTRUCTION_ID, GameItemType.DECONSTRUCTION);
    }
}