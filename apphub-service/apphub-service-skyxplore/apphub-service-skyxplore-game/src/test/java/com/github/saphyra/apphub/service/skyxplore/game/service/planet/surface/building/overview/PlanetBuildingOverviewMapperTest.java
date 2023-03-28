package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewDetailedResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PlanetBuildingOverviewMapperTest {
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private BuildingDetailsMapper buildingDetailsMapper;

    @InjectMocks
    private PlanetBuildingOverviewMapper underTest;

    @Mock
    private Surface surface;

    @Mock
    private Surface surfaceWithDifferentType;

    @Mock
    private Building building;

    @Mock
    private PlanetBuildingOverviewDetailedResponse overviewDetailedResponse;

    @Mock
    private GameData gameData;

    @Mock
    private Buildings buildings;

    @Mock
    private Surfaces surfaces;

    @Test
    public void createOverview() {
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.getByLocation(PLANET_ID)).willReturn(List.of(building, building, building));

        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.getByPlanetId(PLANET_ID)).willReturn(List.of(surfaceWithDifferentType, surface, surface, surface, surface));
        given(surface.getSurfaceType()).willReturn(SurfaceType.CONCRETE);

        given(buildingDetailsMapper.createBuildingDetails(gameData, PLANET_ID, SurfaceType.CONCRETE)).willReturn(Arrays.asList(overviewDetailedResponse));

        PlanetBuildingOverviewResponse result = underTest.createOverview(gameData, PLANET_ID, SurfaceType.CONCRETE);

        assertThat(result.getBuildingDetails()).containsExactly(overviewDetailedResponse);
        assertThat(result.getSlots()).isEqualTo(4);
        assertThat(result.getUsedSlots()).isEqualTo(3);
    }
}