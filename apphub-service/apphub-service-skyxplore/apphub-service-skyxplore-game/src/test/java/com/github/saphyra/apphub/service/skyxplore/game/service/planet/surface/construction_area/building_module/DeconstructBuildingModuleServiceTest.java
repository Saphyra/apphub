package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModules;
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
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_building_module.DeconstructBuildingModuleProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_building_module.DeconstructBuildingModuleProcessFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeconstructBuildingModuleServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private DeconstructionFactory deconstructionFactory;

    @Mock
    private DeconstructionConverter deconstructionConverter;

    @Mock
    private DeconstructBuildingModuleProcessFactory deconstructBuildingModuleProcessFactory;

    @InjectMocks
    private DeconstructBuildingModuleService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule buildingModule;

    @Mock
    private Planets planets;

    @Mock
    private Planet planet;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction construction;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private DeconstructionModel deconstructionModel;

    @Mock
    private DeconstructBuildingModuleProcess process;

    @Mock
    private Processes processes;

    @Mock
    private ProcessModel processModel;

    @BeforeEach
    void setUp() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.findByBuildingModuleIdValidated(BUILDING_MODULE_ID)).willReturn(buildingModule);
        given(buildingModule.getLocation()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
    }

    @Test
    void forbiddenOperation() {
        given(planet.getOwner()).willReturn(null);

        ExceptionValidator.validateForbiddenOperation(() -> underTest.deconstructBuildingModule(USER_ID, BUILDING_MODULE_ID));
    }

    @Test
    void buildingModuleIsUnderConstruction() {
        given(planet.getOwner()).willReturn(USER_ID);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.of(construction));

        ExceptionValidator.validateForbiddenOperation(() -> underTest.deconstructBuildingModule(USER_ID, BUILDING_MODULE_ID));
    }

    @Test
    void buildingModuleIsAlreadyDeconstructed() {
        given(planet.getOwner()).willReturn(USER_ID);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.empty());
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.of(deconstruction));

        ExceptionValidator.validateForbiddenOperation(() -> underTest.deconstructBuildingModule(USER_ID, BUILDING_MODULE_ID));
    }

    @Test
    void deconstructBuildingModule() {
        given(planet.getOwner()).willReturn(USER_ID);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.empty());
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.empty());
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any())).willAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return ExecutionResult.success(null);
        });
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(deconstructionFactory.create(BUILDING_MODULE_ID, PLANET_ID)).willReturn(deconstruction);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(deconstructionConverter.toModel(GAME_ID, deconstruction)).willReturn(deconstructionModel);
        given(deconstructBuildingModuleProcessFactory.create(game, deconstruction)).willReturn(process);
        given(process.toModel()).willReturn(processModel);
        given(gameData.getProcesses()).willReturn(processes);
        given(buildingModule.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID);

        assertThat(underTest.deconstructBuildingModule(USER_ID, BUILDING_MODULE_ID)).isEqualTo(CONSTRUCTION_AREA_ID);

        then(deconstructions).should().add(deconstruction);
        then(progressDiff).should().save(deconstructionModel);
        then(processes).should().add(process);
        then(progressDiff).should().save(processModel);
    }
}