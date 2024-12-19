package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.building;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModuleFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModules;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreaFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreas;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.building.BuildingFiller.HQ_MODULES;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BuildingFillerTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();

    @Mock
    private EmptySurfaceProvider emptySurfaceProvider;

    @Mock
    private ConstructionAreaFactory constructionAreaFactory;

    @Mock
    private BuildingModuleFactory buildingModuleFactory;

    @InjectMocks
    private BuildingFiller underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Planets planets;

    @Mock
    private Planet planet;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Surface surface;

    @Mock
    private ConstructionAreas constructionAreas;

    @Mock
    private ConstructionArea constructionArea;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule buildingModule;

    @Test
    void fillBuildings() {
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.values()).willReturn(List.of(planet));
        given(planet.getOwner()).willReturn(USER_ID);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(surfaces.getByPlanetId(PLANET_ID)).willReturn(List.of(surface));
        given(emptySurfaceProvider.getEmptySurfaceForType(SurfaceType.DESERT, List.of(surface), gameData)).willReturn(surface);
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);
        given(constructionAreaFactory.create(PLANET_ID, SURFACE_ID, GameConstants.DATA_ID_HEADQUARTERS)).willReturn(constructionArea);
        given(constructionArea.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID);
        given(buildingModuleFactory.create(eq(PLANET_ID), eq(CONSTRUCTION_AREA_ID), anyString())).willReturn(buildingModule);
        given(gameData.getBuildingModules()).willReturn(buildingModules);

        underTest.fillBuildings(gameData);

        then(constructionAreas).should().add(constructionArea);
        then(buildingModules).should(times(HQ_MODULES.size())).add(buildingModule);
        HQ_MODULES.forEach(dataId -> then(buildingModuleFactory).should().create(PLANET_ID, CONSTRUCTION_AREA_ID, dataId));
    }
}