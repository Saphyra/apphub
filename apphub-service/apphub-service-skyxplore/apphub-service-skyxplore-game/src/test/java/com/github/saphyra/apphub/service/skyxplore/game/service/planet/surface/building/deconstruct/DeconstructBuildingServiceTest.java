package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruction.DeconstructionProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruction.DeconstructionProcessFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeconstructBuildingServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private DeconstructionFactory deconstructionFactory;

    @Mock
    private DeconstructionProcessFactory deconstructionProcessFactory;

    @Mock
    private DeconstructionPreconditions deconstructionPreconditions;

    @Mock
    private DeconstructionConverter deconstructionConverter;

    @InjectMocks
    private DeconstructBuildingService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Building building;

    @Mock
    private Construction construction;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private ExecutionResult<Void> executionResult;

    @Mock
    private DeconstructionProcess deconstructionProcess;

    @Mock
    private Processes processes;

    @Mock
    private Constructions constructions;

    @Mock
    private Buildings buildings;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private DeconstructionModel deconstructionModel;

    @Mock
    private ProcessModel processModel;

    @Mock
    private Planets planets;

    @Mock
    private Planet planet;

    @BeforeEach
    void setUp() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);
    }

    @Test
    void forbiddenOperation() {
        given(planet.getOwner()).willReturn(UUID.randomUUID());

        ExceptionValidator.validateForbiddenOperation(() -> underTest.deconstructBuilding(USER_ID, PLANET_ID, BUILDING_ID));
    }

    @Test
    void constructionInProgress() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.of(construction));

        Throwable ex = catchThrowable(() -> underTest.deconstructBuilding(USER_ID, PLANET_ID, BUILDING_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    void deconstructBuilding() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.empty());
        given(deconstructionFactory.create(BUILDING_ID, PLANET_ID)).willReturn(deconstruction);
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findByBuildingId(BUILDING_ID)).willReturn(building);
        given(deconstruction.getDeconstructionId()).willReturn(DECONSTRUCTION_ID);

        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any(Runnable.class))).willReturn(executionResult);
        given(deconstructionProcessFactory.create(game, PLANET_ID, DECONSTRUCTION_ID)).willReturn(deconstructionProcess);
        given(gameData.getProcesses()).willReturn(processes);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(game.getGameId()).willReturn(GAME_ID);
        given(deconstructionConverter.toModel(GAME_ID, deconstruction)).willReturn(deconstructionModel);
        given(deconstructionProcess.toModel()).willReturn(processModel);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.deconstructBuilding(USER_ID, PLANET_ID, BUILDING_ID);

        verify(deconstructionPreconditions).checkIfBuildingCanBeDeconstructed(gameData, building);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).processWithWait(argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();

        verify(executionResult).getOrThrow();
        verify(deconstructions).add(deconstruction);
        then(progressDiff).should().save(processModel);
        then(progressDiff).should().save(deconstructionModel);
        verify(processes).add(deconstructionProcess);
    }
}