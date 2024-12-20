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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CancelConstructionOfBuildingModuleServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private AllocationRemovalService allocationRemovalService;

    @InjectMocks
    private CancelConstructionOfBuildingModuleService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction construction;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule buildingModule;

    @Mock
    private Planets planets;

    @Mock
    private Planet planet;

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
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByConstructionIdValidated(CONSTRUCTION_ID)).willReturn(construction);
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(construction.getExternalReference()).willReturn(BUILDING_MODULE_ID);
        given(buildingModules.findByBuildingModuleIdValidated(BUILDING_MODULE_ID)).willReturn(buildingModule);
        given(gameData.getPlanets()).willReturn(planets);
        given(construction.getLocation()).willReturn(PLANET_ID);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
    }

    @Test
    void forbiddenOperation() {
        given(planet.getOwner()).willReturn(UUID.randomUUID());

        ExceptionValidator.validateForbiddenOperation(() -> underTest.cancelConstruction(USER_ID, CONSTRUCTION_ID));
    }

    @Test
    void cancelConstruction() {
        given(planet.getOwner()).willReturn(USER_ID);
        given(buildingModule.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any())).willAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class)
                .run();
            return ExecutionResult.success(null);
        });
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.findByExternalReferenceAndTypeValidated(CONSTRUCTION_ID, ProcessType.CONSTRUCT_BUILDING_MODULE)).willReturn(process);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);

        assertThat(underTest.cancelConstruction(USER_ID, CONSTRUCTION_ID)).isEqualTo(CONSTRUCTION_AREA_ID);

        then(process).should().cleanup();
        then(constructions).should().remove(construction);
        then(allocationRemovalService).should().removeAllocationsAndReservations(progressDiff, gameData, CONSTRUCTION_ID);
        then(progressDiff).should().delete(CONSTRUCTION_ID, GameItemType.CONSTRUCTION);
        then(buildingModules).should().remove(buildingModule);
        then(progressDiff).should().delete(BUILDING_MODULE_ID, GameItemType.BUILDING_MODULE);
    }
}