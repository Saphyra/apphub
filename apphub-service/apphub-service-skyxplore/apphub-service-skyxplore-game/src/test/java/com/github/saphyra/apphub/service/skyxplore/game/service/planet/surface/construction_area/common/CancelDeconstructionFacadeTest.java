package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.common;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModules;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CancelDeconstructionFacadeTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_DECONSTRUCTION_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @InjectMocks
    private CancelDeconstructionFacade underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction constructionAreaDeconstruction;

    @Mock
    private Planets planets;

    @Mock
    private Planet planet;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private Processes processes;

    @Mock
    private Process constructionAreaProcess;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule buildingModule;

    @Mock
    private Deconstruction buildingModuleDeconstruction;

    @Mock
    private Process buildingModuleProcess;

    @Test
    void cancelDeconstructionOfConstructionArea_forbiddenOperation() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByDeconstructionIdValidated(CONSTRUCTION_AREA_DECONSTRUCTION_ID)).willReturn(constructionAreaDeconstruction);
        given(constructionAreaDeconstruction.getLocation()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(UUID.randomUUID());

        ExceptionValidator.validateForbiddenOperation(() -> underTest.cancelDeconstructionOfConstructionArea(USER_ID, CONSTRUCTION_AREA_DECONSTRUCTION_ID));
    }

    @Test
    void cancelDeconstructionOfConstructionArea() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByDeconstructionIdValidated(CONSTRUCTION_AREA_DECONSTRUCTION_ID)).willReturn(constructionAreaDeconstruction);
        given(constructionAreaDeconstruction.getLocation()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any())).willAnswer(invocationOnMock -> {
            invocationOnMock.getArgument(0, Runnable.class).run();
            return ExecutionResult.success(null);
        });
        given(constructionAreaDeconstruction.getDeconstructionId()).willReturn(CONSTRUCTION_AREA_DECONSTRUCTION_ID);
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.findByExternalReferenceAndTypeValidated(CONSTRUCTION_AREA_DECONSTRUCTION_ID, ProcessType.DECONSTRUCT_CONSTRUCTION_AREA)).willReturn(constructionAreaProcess);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(constructionAreaDeconstruction.getExternalReference()).willReturn(CONSTRUCTION_AREA_ID);
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByConstructionAreaId(CONSTRUCTION_AREA_ID)).willReturn(List.of(buildingModule));
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(deconstructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.of(buildingModuleDeconstruction));
        given(buildingModuleDeconstruction.getDeconstructionId()).willReturn(BUILDING_MODULE_DECONSTRUCTION_ID);
        given(processes.findByExternalReferenceAndTypeValidated(BUILDING_MODULE_DECONSTRUCTION_ID, ProcessType.DECONSTRUCT_BUILDING_MODULE)).willReturn(buildingModuleProcess);

        underTest.cancelDeconstructionOfConstructionArea(USER_ID, CONSTRUCTION_AREA_DECONSTRUCTION_ID);

        then(constructionAreaProcess).should().cleanup();
        then(deconstructions).should().remove(constructionAreaDeconstruction);
        then(progressDiff).should().delete(CONSTRUCTION_AREA_DECONSTRUCTION_ID, GameItemType.DECONSTRUCTION);
        then(buildingModuleProcess).should().cleanup();
        then(deconstructions).should().remove(buildingModuleDeconstruction);
        then(progressDiff).should().delete(BUILDING_MODULE_DECONSTRUCTION_ID, GameItemType.DECONSTRUCTION);
    }

    @Test
    void cancelDeconstructionOfBuildingModule_forbiddenOperation() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByDeconstructionIdValidated(BUILDING_MODULE_DECONSTRUCTION_ID)).willReturn(buildingModuleDeconstruction);
        given(buildingModuleDeconstruction.getLocation()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(UUID.randomUUID());

        ExceptionValidator.validateForbiddenOperation(() -> underTest.cancelDeconstructionOfBuildingModule(USER_ID, BUILDING_MODULE_DECONSTRUCTION_ID));
    }

    @Test
    void cancelDeconstructionOfBuildingModule_constructionAreaNotUnderDeconstruction() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByDeconstructionIdValidated(BUILDING_MODULE_DECONSTRUCTION_ID)).willReturn(buildingModuleDeconstruction);
        given(buildingModuleDeconstruction.getLocation()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModuleDeconstruction.getExternalReference()).willReturn(BUILDING_MODULE_ID);
        given(buildingModules.findByBuildingModuleIdValidated(BUILDING_MODULE_ID)).willReturn(buildingModule);
        given(buildingModule.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any())).willAnswer(invocationOnMock -> {
            invocationOnMock.getArgument(0, Runnable.class).run();
            return ExecutionResult.success(null);
        });
        given(gameData.getProcesses()).willReturn(processes);
        given(buildingModuleDeconstruction.getDeconstructionId()).willReturn(BUILDING_MODULE_DECONSTRUCTION_ID);
        given(processes.findByExternalReferenceAndTypeValidated(BUILDING_MODULE_DECONSTRUCTION_ID, ProcessType.DECONSTRUCT_BUILDING_MODULE)).willReturn(buildingModuleProcess);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(deconstructions.findByExternalReference(CONSTRUCTION_AREA_ID)).willReturn(Optional.empty());

        underTest.cancelDeconstructionOfBuildingModule(USER_ID, BUILDING_MODULE_DECONSTRUCTION_ID);

        then(buildingModuleProcess).should().cleanup();
        then(deconstructions).should().remove(buildingModuleDeconstruction);
        then(progressDiff).should().delete(BUILDING_MODULE_DECONSTRUCTION_ID, GameItemType.DECONSTRUCTION);
    }

    @Test
    void cancelDeconstructionOfBuildingModule_constructionAreaUnderDeconstruction() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByDeconstructionIdValidated(BUILDING_MODULE_DECONSTRUCTION_ID)).willReturn(buildingModuleDeconstruction);
        given(buildingModuleDeconstruction.getLocation()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModuleDeconstruction.getExternalReference()).willReturn(BUILDING_MODULE_ID);
        given(buildingModules.findByBuildingModuleIdValidated(BUILDING_MODULE_ID)).willReturn(buildingModule);
        given(buildingModule.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any())).willAnswer(invocationOnMock -> {
            invocationOnMock.getArgument(0, Runnable.class).run();
            return ExecutionResult.success(null);
        });
        given(gameData.getProcesses()).willReturn(processes);
        given(buildingModuleDeconstruction.getDeconstructionId()).willReturn(BUILDING_MODULE_DECONSTRUCTION_ID);
        given(processes.findByExternalReferenceAndTypeValidated(BUILDING_MODULE_DECONSTRUCTION_ID, ProcessType.DECONSTRUCT_BUILDING_MODULE)).willReturn(buildingModuleProcess);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(deconstructions.findByExternalReference(CONSTRUCTION_AREA_ID)).willReturn(Optional.of(constructionAreaDeconstruction));
        given(constructionAreaDeconstruction.getDeconstructionId()).willReturn(CONSTRUCTION_AREA_DECONSTRUCTION_ID);
        given(processes.findByExternalReferenceAndTypeValidated(CONSTRUCTION_AREA_DECONSTRUCTION_ID, ProcessType.DECONSTRUCT_CONSTRUCTION_AREA)).willReturn(constructionAreaProcess);
        given(constructionAreaDeconstruction.getExternalReference()).willReturn(CONSTRUCTION_AREA_ID);
        given(buildingModules.getByConstructionAreaId(CONSTRUCTION_AREA_ID)).willReturn(List.of(buildingModule));
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(deconstructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.of(buildingModuleDeconstruction));

        underTest.cancelDeconstructionOfBuildingModule(USER_ID, BUILDING_MODULE_DECONSTRUCTION_ID);

        then(buildingModuleProcess).should(times(2)).cleanup();
        then(deconstructions).should(times(2)).remove(buildingModuleDeconstruction);
        then(progressDiff).should(times(2)).delete(BUILDING_MODULE_DECONSTRUCTION_ID, GameItemType.DECONSTRUCTION);
        then(constructionAreaProcess).should().cleanup();
        then(deconstructions).should().remove(constructionAreaDeconstruction);
        then(progressDiff).should().delete(CONSTRUCTION_AREA_DECONSTRUCTION_ID, GameItemType.DECONSTRUCTION);
    }
}