package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CancelConstructionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private AllocationRemovalService allocationRemovalService;

    @InjectMocks
    private CancelConstructionService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction construction;

    @Mock
    private Buildings buildings;

    @Mock
    private Building building;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private ExecutionResult<Void> executionResult;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Mock
    private Planets planets;

    @Mock
    private Planet planet;

    @BeforeEach
    void setUp() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findByBuildingId(BUILDING_ID)).willReturn(building);

        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any(Runnable.class))).willReturn(executionResult);
        given(gameData.getProcesses()).willReturn(processes);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(processes.findByExternalReferenceAndTypeValidated(CONSTRUCTION_ID, ProcessType.CONSTRUCTION)).willReturn(process);
    }

    @Test
    void cancelUpgradeOfConstruction() {
        given(constructions.findByConstructionIdValidated(CONSTRUCTION_ID)).willReturn(construction);
        given(construction.getExternalReference()).willReturn(BUILDING_ID);
        given(building.getLevel()).willReturn(1);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.cancelConstructionOfConstruction(USER_ID, CONSTRUCTION_ID);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        then(eventLoop).should().processWithWait(argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();

        then(executionResult).should().getOrThrow();
        then(process).should().cleanup();
        then(constructions).should().remove(construction);
        then(allocationRemovalService).should().removeAllocationsAndReservations(progressDiff, gameData, CONSTRUCTION_ID);
        then(progressDiff).should().delete(CONSTRUCTION_ID, GameItemType.CONSTRUCTION);
    }

    @Test
    void cancelConstructionOfBuilding_forbiddenOperation() {
        given(gameData.getPlanets()).willReturn(planets);
        given(building.getLocation()).willReturn(PLANET_ID);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(UUID.randomUUID());

        ExceptionValidator.validateForbiddenOperation(() -> underTest.cancelConstructionOfBuilding(USER_ID, BUILDING_ID));
    }

    @Test
    void cancelConstructionOfBuilding() {
        given(building.getBuildingId()).willReturn(BUILDING_ID);
        given(constructions.findByExternalReferenceValidated(BUILDING_ID)).willReturn(construction);
        given(building.getLevel()).willReturn(0);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(gameData.getPlanets()).willReturn(planets);
        given(building.getLocation()).willReturn(PLANET_ID);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);

        underTest.cancelConstructionOfBuilding(USER_ID, BUILDING_ID);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        then(eventLoop).should().processWithWait(argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();

        then(buildings).should().remove(building);
        then(progressDiff).should().delete(BUILDING_ID, GameItemType.BUILDING);
        then(executionResult).should().getOrThrow();
        then(process).should().cleanup();
        then(constructions).should().remove(construction);
        then(allocationRemovalService).should().removeAllocationsAndReservations(progressDiff, gameData, CONSTRUCTION_ID);
        then(progressDiff).should().delete(CONSTRUCTION_ID, GameItemType.CONSTRUCTION);
    }
}