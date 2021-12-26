package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SurfaceMap;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CancelConstructionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID ALLOCATED_RESOURCE_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private GameDataProxy gameDataProxy;

    @InjectMocks
    private CancelConstructionService underTest;

    @Mock
    private Game game;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @Mock
    private Building building;

    @Mock
    private Construction construction;

    @Mock
    private SurfaceMap surfaceMap;

    @Mock
    private Surface surface;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private AllocatedResource allocatedResource;

    @Before
    public void setUp() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findPlanetByIdAndOwnerValidated(USER_ID, PLANET_ID)).willReturn(planet);
        given(planet.getSurfaces()).willReturn(surfaceMap);
        given(surfaceMap.findByBuildingIdValidated(BUILDING_ID)).willReturn(surface);
        given(surface.getBuilding()).willReturn(building);
    }

    @Test
    public void constructionNotFound() {
        given(building.getConstruction()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.cancelConstruction(USER_ID, PLANET_ID, BUILDING_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void cancelConstruction() {
        given(building.getConstruction()).willReturn(construction);
        given(building.getLevel()).willReturn(0);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(planet.getStorageDetails()).willReturn(storageDetails);

        ReservedStorages reservedStorages = new ReservedStorages(List.of(reservedStorage));
        given(storageDetails.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorage.getExternalReference()).willReturn(CONSTRUCTION_ID);
        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);

        AllocatedResources allocatedResources = new AllocatedResources(CollectionUtils.asList(allocatedResource));
        given(storageDetails.getAllocatedResources()).willReturn(allocatedResources);
        given(allocatedResource.getExternalReference()).willReturn(CONSTRUCTION_ID);
        given(allocatedResource.getAllocatedResourceId()).willReturn(ALLOCATED_RESOURCE_ID);

        underTest.cancelConstruction(USER_ID, PLANET_ID, BUILDING_ID);

        verify(building).setConstruction(null);
        verify(gameDataProxy).deleteItem(RESERVED_STORAGE_ID, GameItemType.RESERVED_STORAGE);
        verify(gameDataProxy).deleteItem(ALLOCATED_RESOURCE_ID, GameItemType.ALLOCATED_RESOURCE);

        assertThat(reservedStorages).isEmpty();
        assertThat(allocatedResources).isEmpty();

        verify(gameDataProxy).deleteItem(CONSTRUCTION_ID, GameItemType.CONSTRUCTION);

        verify(surface).setBuilding(null);
        verify(gameDataProxy).deleteItem(BUILDING_ID, GameItemType.BUILDING);
    }
}