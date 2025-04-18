package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreas;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module.CancelConstructionOfBuildingModuleService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_construction_area.DeconstructConstructionAreaProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_construction_area.DeconstructConstructionAreaProcessFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeconstructConstructionAreaServiceTest {
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private DeconstructionFactory deconstructionFactory;

    @Mock
    private DeconstructionConverter deconstructionConverter;

    @Mock
    private DeconstructConstructionAreaProcessFactory deconstructConstructionAreaProcessFactory;

    @Mock
    private CancelConstructionOfBuildingModuleService cancelConstructionOfBuildingModuleService;

    @InjectMocks
    private DeconstructConstructionAreaService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private ConstructionAreas constructionAreas;

    @Mock
    private ConstructionArea constructionArea;

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
    private DeconstructConstructionAreaProcess process;

    @Mock
    private Processes processes;

    @Mock
    private ProcessModel processModel;

    @Test
    void forbiddenOperation() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findByConstructionAreaIdValidated(CONSTRUCTION_AREA_ID)).willReturn(constructionArea);
        given(constructionArea.getLocation()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(null);

        ExceptionValidator.validateForbiddenOperation(() -> underTest.deconstructConstructionArea(USER_ID, CONSTRUCTION_AREA_ID));
    }

    @Test
    void underConstruction() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findByConstructionAreaIdValidated(CONSTRUCTION_AREA_ID)).willReturn(constructionArea);
        given(constructionArea.getLocation()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(CONSTRUCTION_AREA_ID)).willReturn(Optional.of(construction));

        ExceptionValidator.validateForbiddenOperation(() -> underTest.deconstructConstructionArea(USER_ID, CONSTRUCTION_AREA_ID));
    }

    @Test
    void underDeconstruction() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findByConstructionAreaIdValidated(CONSTRUCTION_AREA_ID)).willReturn(constructionArea);
        given(constructionArea.getLocation()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(CONSTRUCTION_AREA_ID)).willReturn(Optional.empty());
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(CONSTRUCTION_AREA_ID)).willReturn(Optional.of(deconstruction));

        ExceptionValidator.validateForbiddenOperation(() -> underTest.deconstructConstructionArea(USER_ID, CONSTRUCTION_AREA_ID));
    }

    @Test
    void deconstructConstructionArea() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findByConstructionAreaIdValidated(CONSTRUCTION_AREA_ID)).willReturn(constructionArea);
        given(constructionArea.getLocation()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(CONSTRUCTION_AREA_ID)).willReturn(Optional.empty());
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(CONSTRUCTION_AREA_ID)).willReturn(Optional.empty());
        given(game.getEventLoop()).willReturn(eventLoop);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(eventLoop.processWithWait(any())).willAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return ExecutionResult.success(null);
        });
        given(deconstructionFactory.create(CONSTRUCTION_AREA_ID, PLANET_ID)).willReturn(deconstruction);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(deconstructionConverter.toModel(GAME_ID, deconstruction)).willReturn(deconstructionModel);
        given(deconstructConstructionAreaProcessFactory.create(game, deconstruction)).willReturn(process);
        given(gameData.getProcesses()).willReturn(processes);
        given(process.toModel()).willReturn(processModel);

        underTest.deconstructConstructionArea(USER_ID, CONSTRUCTION_AREA_ID);

        then(deconstructions).should().add(deconstruction);
        then(progressDiff).should().save(deconstructionModel);
        then(processes).should().add(process);
        then(progressDiff).should().save(processModel);
        then(cancelConstructionOfBuildingModuleService).should().cancelConstructionOfConstructionAreaBuildingModules(game, CONSTRUCTION_AREA_ID);
    }
}