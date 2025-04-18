package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CancelDeconstructionOfBuildingModuleServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @InjectMocks
    private CancelDeconstructionOfBuildingModuleService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private Planets planets;

    @Mock
    private Planet planet;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule buildingModule;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Mock
    private GameProgressDiff progressDiff;

    @BeforeEach
    void setUp() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
    }

    @Test
    void forbiddenOperation() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(planet.getOwner()).willReturn(null);
        given(deconstructions.findByDeconstructionIdValidated(DECONSTRUCTION_ID)).willReturn(deconstruction);
        given(deconstruction.getLocation()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);

        ExceptionValidator.validateForbiddenOperation(() -> underTest.cancelDeconstruction(USER_ID, DECONSTRUCTION_ID));
    }

    @Test
    void cancelDeconstruction() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(deconstructions.findByDeconstructionIdValidated(DECONSTRUCTION_ID)).willReturn(deconstruction);
        given(deconstruction.getLocation()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);

        given(planet.getOwner()).willReturn(USER_ID);
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(deconstruction.getDeconstructionId()).willReturn(DECONSTRUCTION_ID);
        given(deconstruction.getExternalReference()).willReturn(BUILDING_MODULE_ID);
        given(buildingModules.findByBuildingModuleIdValidated(BUILDING_MODULE_ID)).willReturn(buildingModule);
        given(buildingModule.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any())).willAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();

            return ExecutionResult.success(null);
        });
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.findByExternalReferenceAndTypeValidated(DECONSTRUCTION_ID, ProcessType.DECONSTRUCT_BUILDING_MODULE)).willReturn(process);
        given(game.getProgressDiff()).willReturn(progressDiff);

        assertThat(underTest.cancelDeconstruction(USER_ID, DECONSTRUCTION_ID)).isEqualTo(CONSTRUCTION_AREA_ID);

        then(process).should().cleanup();
        then(deconstructions).should().remove(deconstruction);
        then(progressDiff).should().delete(DECONSTRUCTION_ID, GameItemType.DECONSTRUCTION);
    }

    @Test
    void cancelDeconstructionOfConstructionAreaBuildingModules(){
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByConstructionAreaId(CONSTRUCTION_AREA_ID)).willReturn(List.of(buildingModule, buildingModule));
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(deconstructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.of(deconstruction)).willReturn(Optional.empty());
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.findByExternalReferenceAndTypeValidated(DECONSTRUCTION_ID, ProcessType.DECONSTRUCT_BUILDING_MODULE)).willReturn(process);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(deconstruction.getDeconstructionId()).willReturn(DECONSTRUCTION_ID);

        underTest.cancelDeconstructionOfConstructionAreaBuildingModules(game, CONSTRUCTION_AREA_ID);
    }
}