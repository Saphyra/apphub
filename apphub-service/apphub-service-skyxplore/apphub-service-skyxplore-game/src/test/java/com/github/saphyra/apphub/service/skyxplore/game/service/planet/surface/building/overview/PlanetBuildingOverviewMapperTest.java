package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewDetailedResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class PlanetBuildingOverviewMapperTest {
    @Mock
    private BuildingDetailsMapper buildingDetailsMapper;

    @InjectMocks
    private PlanetBuildingOverviewMapper underTest;

    @Mock
    private Surface surfaceWithoutBuilding;

    @Mock
    private Surface surfaceWithBuilding;

    @Mock
    private Building building;

    @Mock
    private PlanetBuildingOverviewDetailedResponse overviewDetailedResponse;

    @Test
    public void createOverview() {
        given(surfaceWithBuilding.getBuilding()).willReturn(building);
        given(buildingDetailsMapper.createBuildingDetails(Arrays.asList(surfaceWithBuilding, surfaceWithoutBuilding))).willReturn(Arrays.asList(overviewDetailedResponse));

        PlanetBuildingOverviewResponse result = underTest.createOverview(Arrays.asList(surfaceWithBuilding, surfaceWithoutBuilding));

        assertThat(result.getBuildingDetails()).containsExactly(overviewDetailedResponse);
        assertThat(result.getSlots()).isEqualTo(2);
        assertThat(result.getUsedSlots()).isEqualTo(1);
    }
}