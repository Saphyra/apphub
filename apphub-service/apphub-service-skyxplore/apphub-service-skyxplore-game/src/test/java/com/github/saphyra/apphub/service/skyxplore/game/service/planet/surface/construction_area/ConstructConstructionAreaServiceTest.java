package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionAreaModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.construction_area.ConstructionAreaData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.construction_area.ConstructionAreaDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreaConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreaFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreas;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption.ResourceAllocationService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_construction_area.ConstructConstructionAreaProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_construction_area.ConstructConstructionAreaProcessFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ConstructConstructionAreaServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final String CONSTRUCTION_AREA_DATA_ID = "construction-area-data-id";
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 2;
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final Integer RESOURCE_AMOUNT = 243;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Spy
    private final ConstructionAreaDataService constructionAreaDataService = new ConstructionAreaDataService();

    @Mock
    private GameDao gameDao;

    @Mock
    private ConstructionFactory constructionFactory;

    @Mock
    private ConstructionAreaFactory constructionAreaFactory;

    @Mock
    private ResourceAllocationService resourceAllocationService;

    @Mock
    private ConstructConstructionAreaProcessFactory constructConstructionAreaProcessFactory;

    @Mock
    private ConstructionConverter constructionConverter;

    @Mock
    private ConstructionAreaConverter constructionAreaConverter;

    @InjectMocks
    private ConstructConstructionAreaService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Surface surface;

    @Mock
    private Planets planets;

    @Mock
    private Planet planet;

    @Mock
    private ConstructionAreaData constructionAreaData;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction construction;

    @Mock
    private ConstructionAreas constructionAreas;

    @Mock
    private ConstructionArea constructionArea;

    @Test
    void nullDataId() {
        ExceptionValidator.validateInvalidParam(() -> underTest.constructConstructionArea(USER_ID, SURFACE_ID, null), "dataId", "must not be null");
    }

    @Test
    void invalidDataId() {
        ExceptionValidator.validateInvalidParam(() -> underTest.constructConstructionArea(USER_ID, SURFACE_ID, "asd"), "dataId", "invalid value");
    }

    @Test
    void forbiddenOperation() {
        constructionAreaDataService.put(CONSTRUCTION_AREA_DATA_ID, constructionAreaData);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findByIdValidated(SURFACE_ID)).willReturn(surface);
        given(surface.getPlanetId()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(null);

        ExceptionValidator.validateForbiddenOperation(() -> underTest.constructConstructionArea(USER_ID, SURFACE_ID, CONSTRUCTION_AREA_DATA_ID));
    }

    @Test
    void terraformationInProgress(){
        constructionAreaDataService.put(CONSTRUCTION_AREA_DATA_ID, constructionAreaData);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findByIdValidated(SURFACE_ID)).willReturn(surface);
        given(surface.getPlanetId()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.of(construction));

        ExceptionValidator.validateNotLoggedException(() -> underTest.constructConstructionArea(USER_ID, SURFACE_ID, CONSTRUCTION_AREA_DATA_ID), HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    void surfaceOccupied(){
        constructionAreaDataService.put(CONSTRUCTION_AREA_DATA_ID, constructionAreaData);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findByIdValidated(SURFACE_ID)).willReturn(surface);
        given(surface.getPlanetId()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.empty());
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findBySurfaceId(SURFACE_ID)).willReturn(Optional.of(constructionArea));

        ExceptionValidator.validateForbiddenOperation(() -> underTest.constructConstructionArea(USER_ID, SURFACE_ID, CONSTRUCTION_AREA_DATA_ID));
    }

    @Test
    void unsupportedSurfaceType(){
        constructionAreaDataService.put(CONSTRUCTION_AREA_DATA_ID, constructionAreaData);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findByIdValidated(SURFACE_ID)).willReturn(surface);
        given(surface.getPlanetId()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.empty());
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findBySurfaceId(SURFACE_ID)).willReturn(Optional.empty());
        given(constructionAreaData.getSupportedSurfaces()).willReturn(Collections.emptyList());
        given(surface.getSurfaceType()).willReturn(SurfaceType.DESERT);

        ExceptionValidator.validateForbiddenOperation(() -> underTest.constructConstructionArea(USER_ID, SURFACE_ID, CONSTRUCTION_AREA_DATA_ID));
    }

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private ConstructConstructionAreaProcess process;

    @Mock
    private Processes processes;

    @Mock
    private ProcessModel processModel;

    @Mock
    private ConstructionAreaModel constructionAreaModel;

    @Mock
    private ConstructionModel constructionModel;

    @Test
    void constructConstructionArea(){
        constructionAreaDataService.put(CONSTRUCTION_AREA_DATA_ID, constructionAreaData);
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findByIdValidated(SURFACE_ID)).willReturn(surface);
        given(surface.getPlanetId()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.findByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getOwner()).willReturn(USER_ID);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.empty());
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findBySurfaceId(SURFACE_ID)).willReturn(Optional.empty());
        given(constructionAreaData.getSupportedSurfaces()).willReturn(List.of(SurfaceType.DESERT));
        given(surface.getSurfaceType()).willReturn(SurfaceType.DESERT);
        given(constructionAreaData.getConstructionRequirements()).willReturn(constructionRequirements);
        given(constructionAreaFactory.create(PLANET_ID, SURFACE_ID, CONSTRUCTION_AREA_DATA_ID)).willReturn(constructionArea);
        given(constructionArea.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID);
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(constructionFactory.create(CONSTRUCTION_AREA_ID, ConstructionType.CONSTRUCTION_AREA, PLANET_ID, REQUIRED_WORK_POINTS)).willReturn(construction);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any())).willAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return ExecutionResult.success(null);
        });
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(constructConstructionAreaProcessFactory.create(game, construction)).willReturn(process);
        given(constructionRequirements.getRequiredResources()).willReturn(Map.of(RESOURCE_DATA_ID, RESOURCE_AMOUNT));
        given(gameData.getProcesses()).willReturn(processes);
        given(process.toModel()).willReturn(processModel);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(constructionAreaConverter.convert(GAME_ID, constructionArea)).willReturn(constructionAreaModel);
        given(constructionConverter.toModel(GAME_ID, construction)).willReturn(constructionModel);

        underTest.constructConstructionArea(USER_ID, SURFACE_ID, CONSTRUCTION_AREA_DATA_ID);

        then(resourceAllocationService).should().processResourceRequirements(progressDiff, gameData, PLANET_ID, CONSTRUCTION_ID, Map.of(RESOURCE_DATA_ID, RESOURCE_AMOUNT));
        then(constructionAreas).should().add(constructionArea);
        then(processes).should().add(process);
        then(constructions).should().add(construction);
        then(progressDiff).should().save(processModel);
        then(progressDiff).should().save(constructionAreaModel);
        then(progressDiff).should().save(constructionModel);
    }
}