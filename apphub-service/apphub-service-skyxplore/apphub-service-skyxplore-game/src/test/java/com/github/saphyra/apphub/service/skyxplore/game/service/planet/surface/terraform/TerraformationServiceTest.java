package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilities;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilitiesService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibility;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SurfaceMap;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ConstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation.SurfaceToQueueItemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption.ResourceConsumptionService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ConstructionToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TerraformationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 436;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();

    @Mock
    private TerraformingPossibilitiesService terraformingPossibilitiesService;

    @Mock
    private GameDao gameDao;

    @Mock
    private ResourceConsumptionService resourceConsumptionService;

    @Mock
    private ConstructionFactory constructionFactory;

    @Mock
    private GameDataProxy gameDataProxy;

    @Mock
    private ConstructionToModelConverter constructionToModelConverter;

    @Mock
    private SurfaceToResponseConverter surfaceToResponseConverter;

    @Mock
    private SurfaceToQueueItemConverter surfaceToQueueItemConverter;

    @Mock
    private QueueItemToResponseConverter queueItemToResponseConverter;

    @Mock
    private WsMessageSender wsMessageSender;

    @InjectMocks
    private TerraformationService underTest;

    @Mock
    private Game game;

    @Mock
    private Universe universe;
    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private Building building;

    @Mock
    private Construction construction;

    @Mock
    private TerraformingPossibility terraformingPossibility;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private ConstructionModel constructionModel;

    @Mock
    private QueueItem queueItem;

    @Mock
    private QueueResponse queueResponse;

    @Mock
    private SurfaceResponse surfaceResponse;

    @Before
    public void setUp() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(game.getGameId()).willReturn(GAME_ID);
        given(universe.findByOwnerAndPlanetIdValidated(USER_ID, PLANET_ID)).willReturn(planet);
        given(planet.getSurfaces()).willReturn(new SurfaceMap(CollectionUtils.singleValueMap(GameConstants.ORIGO, surface)));
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);
        given(surface.getSurfaceType()).willReturn(SurfaceType.DESERT);
    }

    @Test
    public void invalidSurfaceType() {
        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, "afe"));

        ExceptionValidator.validateInvalidParam(ex, "surfaceType", "invalid value");
    }

    @Test
    public void terraformationAlreadyInProgress() {
        given(surface.getTerraformation()).willReturn(construction);

        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name()));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    public void surfaceNotEmpty() {
        given(surface.getBuilding()).willReturn(building);

        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name()));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void surfaceTypeCannotBeTerraformed() {
        given(terraformingPossibilitiesService.getOptional(SurfaceType.DESERT)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name()));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void surfaceCannotBeTerraformedToGivenType() {
        given(terraformingPossibilitiesService.getOptional(SurfaceType.DESERT)).willReturn(Optional.of(new TerraformingPossibilities()));

        Throwable ex = catchThrowable(() -> underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name()));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void terraform() {
        given(terraformingPossibilitiesService.getOptional(SurfaceType.DESERT)).willReturn(Optional.of(new TerraformingPossibilities(List.of(terraformingPossibility))));
        given(terraformingPossibility.getSurfaceType()).willReturn(SurfaceType.CONCRETE);
        given(terraformingPossibility.getConstructionRequirements()).willReturn(constructionRequirements);
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(constructionFactory.create(SURFACE_ID, REQUIRED_WORK_POINTS, SurfaceType.CONCRETE.name())).willReturn(construction);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(constructionRequirements.getRequiredResources()).willReturn(Collections.emptyMap());
        given(constructionToModelConverter.convert(construction, GAME_ID)).willReturn(constructionModel);
        given(surfaceToQueueItemConverter.convert(surface)).willReturn(queueItem);
        given(queueItemToResponseConverter.convert(queueItem, planet)).willReturn(queueResponse);
        given(surfaceToResponseConverter.convert(surface)).willReturn(surfaceResponse);

        SurfaceResponse result = underTest.terraform(USER_ID, PLANET_ID, SURFACE_ID, SurfaceType.CONCRETE.name());

        assertThat(result).isEqualTo(surfaceResponse);

        verify(resourceConsumptionService).processResourceRequirements(GAME_ID, planet, LocationType.PLANET, CONSTRUCTION_ID, Collections.emptyMap());
        verify(surface).setTerraformation(construction);
        verify(gameDataProxy).saveItem(constructionModel);
        verify(wsMessageSender).planetQueueItemModified(USER_ID, PLANET_ID, queueResponse);
    }
}