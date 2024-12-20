package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CancelDeconstructConstructionAreaServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @InjectMocks
    private CancelDeconstructConstructionAreaService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private Planets planets;

    @Mock
    private Planet planet;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Test
    void forbiddenOperation() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByDeconstructionIdValidated(DECONSTRUCTION_ID)).willReturn(deconstruction);
        given(deconstruction.getLocation()).willReturn(LOCATION);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(LOCATION)).willReturn(planet);
        given(planet.getOwner()).willReturn(null);

        ExceptionValidator.validateForbiddenOperation(() -> underTest.cancelDeconstruction(USER_ID, DECONSTRUCTION_ID));
    }

    @Test
    void cancelDeconstruction() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByDeconstructionIdValidated(DECONSTRUCTION_ID)).willReturn(deconstruction);
        given(deconstruction.getLocation()).willReturn(LOCATION);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(LOCATION)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any())).willAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return ExecutionResult.success(null);
        });
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.findByExternalReferenceAndTypeValidated(DECONSTRUCTION_ID, ProcessType.DECONSTRUCT_CONSTRUCTION_AREA)).willReturn(process);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.cancelDeconstruction(USER_ID, DECONSTRUCTION_ID);

        then(process).should().cleanup();
        then(deconstructions).should().remove(deconstruction);
        then(progressDiff).should().delete(DECONSTRUCTION_ID, GameItemType.DECONSTRUCTION);
    }
}