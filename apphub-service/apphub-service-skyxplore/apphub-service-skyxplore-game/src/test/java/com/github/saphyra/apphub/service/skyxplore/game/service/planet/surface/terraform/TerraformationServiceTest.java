package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilities;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilitiesService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibility;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreas;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.terraformation.TerraformationProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.terraformation.TerraformationProcessFactory;
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
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TerraformationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 436;
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private TerraformingPossibilitiesService terraformingPossibilitiesService;

    @Mock
    private GameDao gameDao;

    @Mock
    private ConstructionFactory constructionFactory;

    @Mock
    private TerraformationProcessFactory terraformationProcessFactory;

    @Mock
    private ConstructionConverter constructionConverter;

    @InjectMocks
    private TerraformationService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Surface surface;

    @Mock
    private ConstructionArea constructionArea;

    @Mock
    private Construction terraformation;

    @Mock
    private TerraformingPossibility terraformingPossibility;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private TerraformationProcess terraformationProcess;

    @Mock
    private Processes processes;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private ExecutionResult<Void> executionResult;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Constructions constructions;

    @Mock
    private ConstructionAreas constructionAreas;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private ProcessModel processModel;

    @Mock
    private ConstructionModel constructionModel;

    @Mock
    private Planets planets;

    @Mock
    private Planet planet;

    @Mock
    private StoredResources storedResources;

    @Mock
    private StoredResource storedResource;

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

        ExceptionValidator.validateForbiddenOperation(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name()));
    }

    @Test
    public void invalidSurfaceType() {
        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, "afe"));

        ExceptionValidator.validateInvalidParam(ex, "surfaceType", "invalid value");
    }

    @Test
    public void terraformationAlreadyInProgress() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.of(terraformation));

        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name()));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    public void surfaceNotEmpty() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.empty());
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findBySurfaceId(SURFACE_ID)).willReturn(Optional.of(constructionArea));

        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name()));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    void leftoverResources() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.empty());
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findBySurfaceId(SURFACE_ID)).willReturn(Optional.empty());
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.getByContainerId(SURFACE_ID)).willReturn(List.of(storedResource));
        given(storedResource.getAmount()).willReturn(5);

        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name()));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void surfaceTypeCannotBeTerraformed() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.empty());
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findBySurfaceId(SURFACE_ID)).willReturn(Optional.empty());
        given(surface.getSurfaceType()).willReturn(SurfaceType.DESERT);
        given(terraformingPossibilitiesService.getOptional(SurfaceType.DESERT)).willReturn(Optional.empty());
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findByIdValidated(SURFACE_ID)).willReturn(surface);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.getByContainerId(SURFACE_ID)).willReturn(List.of());

        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name()));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void surfaceCannotBeTerraformedToGivenType() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.empty());
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findBySurfaceId(SURFACE_ID)).willReturn(Optional.empty());
        given(surface.getSurfaceType()).willReturn(SurfaceType.DESERT);
        given(terraformingPossibilitiesService.getOptional(SurfaceType.DESERT)).willReturn(Optional.of(new TerraformingPossibilities()));
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findByIdValidated(SURFACE_ID)).willReturn(surface);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.getByContainerId(SURFACE_ID)).willReturn(List.of());

        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name()));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void terraform() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.empty());
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findBySurfaceId(SURFACE_ID)).willReturn(Optional.empty());
        given(surface.getSurfaceType()).willReturn(SurfaceType.DESERT);
        given(terraformingPossibilitiesService.getOptional(SurfaceType.DESERT)).willReturn(Optional.of(new TerraformingPossibilities(List.of(terraformingPossibility))));
        given(terraformingPossibility.getSurfaceType()).willReturn(SurfaceType.CONCRETE);
        given(terraformingPossibility.getConstructionRequirements()).willReturn(constructionRequirements);
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(constructionFactory.create(SURFACE_ID, PLANET_ID, REQUIRED_WORK_POINTS, SurfaceType.CONCRETE.name())).willReturn(terraformation);
        given(terraformation.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(constructionRequirements.getRequiredResources()).willReturn(Collections.emptyMap());
        given(terraformationProcessFactory.create(game, PLANET_ID, terraformation)).willReturn(terraformationProcess);
        given(gameData.getProcesses()).willReturn(processes);
        given(game.getEventLoop()).willReturn(eventLoop);
        given(eventLoop.processWithWait(any(Runnable.class))).willReturn(executionResult);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findByIdValidated(SURFACE_ID)).willReturn(surface);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(terraformationProcess.toModel()).willReturn(processModel);
        given(constructionConverter.toModel(GAME_ID, terraformation)).willReturn(constructionModel);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.getByContainerId(SURFACE_ID)).willReturn(List.of());

        underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name());

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).processWithWait(argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();

        verify(processes).add(terraformationProcess);
        verify(executionResult).getOrThrow();
        then(progressDiff).should().save(processModel);
        then(progressDiff).should().save(constructionModel);
    }
}