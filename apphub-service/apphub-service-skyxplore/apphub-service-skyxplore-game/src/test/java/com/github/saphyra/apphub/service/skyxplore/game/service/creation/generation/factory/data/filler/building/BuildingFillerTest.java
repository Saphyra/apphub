package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.building;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.miscellaneous.MiscellaneousBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.miscellaneous.MiscellaneousBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.BuildingFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BuildingFillerTest {
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String DEFAULT_BUILDING_DATA_ID = "default-building-data-id";
    private static final UUID CONCRETE_SURFACE_ID = UUID.randomUUID();
    private static final UUID MOUNTAIN_SURFACE_ID = UUID.randomUUID();
    private static final UUID ORE_FIELD_SURFACE_ID = UUID.randomUUID();

    @Mock
    private MiscellaneousBuildingService miscellaneousBuildingService;

    @Mock
    private BuildingFactory buildingFactory;

    @Mock
    private EmptySurfaceProvider emptySurfaceProvider;

    private BuildingFiller underTest;

    @Mock
    private MiscellaneousBuilding defaultBuilding;

    @Mock
    private MiscellaneousBuilding otherBuilding;

    @Mock
    private GameData gameData;

    @Mock
    private Planet occupiedPlanet;

    @Mock
    private Planet emptyPlanet;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Surface mountainSurface;

    @Mock
    private Surface oreFieldSurface;

    @Mock
    private Surface concreteSurface;

    @Mock
    private Building mountainBuilding;

    @Mock
    private Building oreFieldBuilding;

    @Mock
    private Building concreteBuilding;

    @Mock
    private Buildings buildings;

    @BeforeEach
    void setUp() {
        given(miscellaneousBuildingService.values()).willReturn(List.of(defaultBuilding, otherBuilding));
        given(defaultBuilding.isDefaultBuilding()).willReturn(true);
        given(otherBuilding.isDefaultBuilding()).willReturn(false);

        underTest = BuildingFiller.builder()
            .buildingDataServices(List.of(miscellaneousBuildingService))
            .buildingFactory(buildingFactory)
            .emptySurfaceProvider(emptySurfaceProvider)
            .build();
    }

    @Test
    void fillBuildings() {
        given(gameData.getPlanets()).willReturn(CollectionUtils.toMap(
            new Planets(),
            new BiWrapper<>(UUID.randomUUID(), emptyPlanet),
            new BiWrapper<>(UUID.randomUUID(), occupiedPlanet)
        ));

        given(emptyPlanet.getOwner()).willReturn(null);
        given(occupiedPlanet.getOwner()).willReturn(UUID.randomUUID());

        given(gameData.getSurfaces()).willReturn(surfaces);
        given(occupiedPlanet.getPlanetId()).willReturn(PLANET_ID);
        List<Surface> surfaceList = List.of(mountainSurface, oreFieldSurface, concreteSurface);
        given(surfaces.getByPlanetId(PLANET_ID)).willReturn(surfaceList);

        given(defaultBuilding.getPrimarySurfaceType()).willReturn(SurfaceType.CONCRETE);

        given(emptySurfaceProvider.getEmptySurfaceForType(SurfaceType.CONCRETE, surfaceList, gameData)).willReturn(concreteSurface);
        given(emptySurfaceProvider.getEmptySurfaceForType(SurfaceType.ORE_FIELD, surfaceList, gameData)).willReturn(oreFieldSurface);
        given(emptySurfaceProvider.getEmptySurfaceForType(SurfaceType.MOUNTAIN, surfaceList, gameData)).willReturn(mountainSurface);

        given(defaultBuilding.getId()).willReturn(DEFAULT_BUILDING_DATA_ID);

        given(concreteSurface.getSurfaceId()).willReturn(CONCRETE_SURFACE_ID);
        given(mountainSurface.getSurfaceId()).willReturn(MOUNTAIN_SURFACE_ID);
        given(oreFieldSurface.getSurfaceId()).willReturn(ORE_FIELD_SURFACE_ID);

        given(buildingFactory.create(DEFAULT_BUILDING_DATA_ID, PLANET_ID, CONCRETE_SURFACE_ID)).willReturn(concreteBuilding);
        given(buildingFactory.create(GameConstants.DATA_ID_EXCAVATOR, PLANET_ID, MOUNTAIN_SURFACE_ID)).willReturn(mountainBuilding);
        given(buildingFactory.create(GameConstants.DATA_ID_EXCAVATOR, PLANET_ID, ORE_FIELD_SURFACE_ID)).willReturn(oreFieldBuilding);

        given(gameData.getBuildings()).willReturn(buildings);

        underTest.fillBuildings(gameData);

        verify(buildings).add(concreteBuilding);
        verify(buildings).add(oreFieldBuilding);
        verify(buildings).add(mountainBuilding);
    }
}