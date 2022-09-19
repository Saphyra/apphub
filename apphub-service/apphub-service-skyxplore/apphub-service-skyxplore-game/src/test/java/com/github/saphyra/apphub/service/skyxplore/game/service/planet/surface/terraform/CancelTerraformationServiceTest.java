package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SurfaceMap;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption.CancelAllocationsService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CancelTerraformationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";

    @Mock
    private GameDao gameDao;

    @Mock
    private GameDataProxy gameDataProxy;

    @Mock
    private CancelAllocationsService cancelAllocationsService;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private SurfaceToResponseConverter surfaceToResponseConverter;

    @Mock
    private PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    @InjectMocks
    private CancelTerraformationService underTest;

    @Mock
    private Game game;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private Construction construction;

    @Mock
    private SurfaceResponse surfaceResponse;

    @Mock
    private PlanetBuildingOverviewResponse planetBuildingOverviewResponse;

    @Before
    public void setUp() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findByOwnerAndPlanetIdValidated(USER_ID, PLANET_ID)).willReturn(planet);
        given(planet.getSurfaces()).willReturn(new SurfaceMap(CollectionUtils.singleValueMap(GameConstants.ORIGO, surface)));
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(planet.getOwner()).willReturn(USER_ID);
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);
    }

    @Test
    public void cancelTerraformationQueueItem() {
        given(surface.getTerraformation()).willReturn(construction);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(surfaceToResponseConverter.convert(surface)).willReturn(surfaceResponse);
        given(planetBuildingOverviewQueryService.getBuildingOverview(planet)).willReturn(CollectionUtils.singleValueMap(DATA_ID, planetBuildingOverviewResponse));

        underTest.cancelTerraformationQueueItem(USER_ID, PLANET_ID, CONSTRUCTION_ID);

        verify(cancelAllocationsService).cancelAllocationsAndReservations(planet, CONSTRUCTION_ID);
        verify(surface).setTerraformation(null);
        verify(gameDataProxy).deleteItem(CONSTRUCTION_ID, GameItemType.CONSTRUCTION);
        verify(messageSender).planetSurfaceModified(USER_ID, PLANET_ID, surfaceResponse);
        verify(messageSender).planetBuildingDetailsModified(USER_ID, PLANET_ID, CollectionUtils.singleValueMap(DATA_ID, planetBuildingOverviewResponse));
    }

    @Test
    public void terraformationNotInProgress() {
        given(surface.getTerraformation()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.cancelTerraformationOfSurface(USER_ID, PLANET_ID, SURFACE_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void cancelTerraformationOfSurface() {
        given(surface.getTerraformation()).willReturn(construction);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(planetBuildingOverviewQueryService.getBuildingOverview(planet)).willReturn(CollectionUtils.singleValueMap(DATA_ID, planetBuildingOverviewResponse));

        underTest.cancelTerraformationOfSurface(USER_ID, PLANET_ID, SURFACE_ID);

        verify(cancelAllocationsService).cancelAllocationsAndReservations(planet, CONSTRUCTION_ID);
        verify(surface).setTerraformation(null);
        verify(gameDataProxy).deleteItem(CONSTRUCTION_ID, GameItemType.CONSTRUCTION);
        verify(messageSender).planetBuildingDetailsModified(USER_ID, PLANET_ID, CollectionUtils.singleValueMap(DATA_ID, planetBuildingOverviewResponse));
    }
}