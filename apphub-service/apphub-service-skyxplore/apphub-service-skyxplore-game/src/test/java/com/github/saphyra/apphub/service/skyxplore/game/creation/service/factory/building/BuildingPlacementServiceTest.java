package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.building;

import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.data.AbstractDataService;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BuildingPlacementServiceTest {
    private static final String DATA_ID = "data-id";
    private static final UUID CONCRETE_SURFACE_ID = UUID.randomUUID();
    private static final UUID MOUNTAIN_SURFACE_ID = UUID.randomUUID();
    private static final UUID ORE_FIELD_SURFACE_ID = UUID.randomUUID();

    @Mock
    private BuildingFactory buildingFactory;

    @Mock
    private EmptySurfaceProvider emptySurfaceProvider;

    @Mock
    private AbstractDataService<String, BuildingData> dataService;

    private BuildingPlacementService underTest;

    @Mock
    private BuildingData buildingData;

    @Mock
    private Planet planet;

    @Mock
    private Surface concreteSurface;

    @Mock
    private Surface mountainSurface;

    @Mock
    private Surface oreFieldSurface;

    @Mock
    private Building building;

    @Before
    public void setUp() {
        given(buildingData.isDefaultBuilding()).willReturn(true);
        given(dataService.values()).willReturn(Arrays.asList(buildingData));

        underTest = new BuildingPlacementService(
            Arrays.asList(dataService),
            buildingFactory,
            emptySurfaceProvider
        );
    }

    @Test
    public void placeDefaultBuildings() {
        Map<Coordinate, Surface> surfaceMap = new HashMap<>();
        surfaceMap.put(randomCoordinate(), concreteSurface);
        surfaceMap.put(randomCoordinate(), mountainSurface);
        surfaceMap.put(randomCoordinate(), oreFieldSurface);

        given(planet.getSurfaces()).willReturn(surfaceMap);

        given(concreteSurface.getSurfaceId()).willReturn(CONCRETE_SURFACE_ID);
        given(mountainSurface.getSurfaceId()).willReturn(MOUNTAIN_SURFACE_ID);
        given(oreFieldSurface.getSurfaceId()).willReturn(ORE_FIELD_SURFACE_ID);

        given(buildingData.getId()).willReturn(DATA_ID);

        given(buildingData.getPrimarySurfaceType()).willReturn(SurfaceType.CONCRETE);

        given(emptySurfaceProvider.getEmptySurfaceForType(SurfaceType.CONCRETE, surfaceMap.values())).willReturn(concreteSurface);
        given(emptySurfaceProvider.getEmptySurfaceForType(SurfaceType.MOUNTAIN, surfaceMap.values())).willReturn(mountainSurface);
        given(emptySurfaceProvider.getEmptySurfaceForType(SurfaceType.ORE_FIELD, surfaceMap.values())).willReturn(oreFieldSurface);

        given(buildingFactory.create(DATA_ID, CONCRETE_SURFACE_ID)).willReturn(building);
        given(buildingFactory.create("excavator", MOUNTAIN_SURFACE_ID)).willReturn(building);
        given(buildingFactory.create("excavator", ORE_FIELD_SURFACE_ID)).willReturn(building);

        underTest.placeDefaultBuildings(planet);

        verify(concreteSurface).setBuilding(building);
        verify(mountainSurface).setBuilding(building);
        verify(oreFieldSurface).setBuilding(building);
    }

    private Coordinate randomCoordinate() {
        Random random = new Random();
        return new Coordinate(random.randInt(0, 1000), random.randInt(0, 1000));
    }
}