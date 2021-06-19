package com.github.saphyra.apphub.service.skyxplore.game.service.planet.building_overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewDetailedResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class BuildingDetailsMapperTest {
    private static final String DATA_ID = "data-id";

    @Mock
    private BuildingDetailMapper buildingDetailMapper;

    @InjectMocks
    private BuildingDetailsMapper underTest;

    @Mock
    private Surface surfaceWithoutBuilding;

    @Mock
    private Surface surfaceWithBuilding;

    @Mock
    private Building building;

    @Mock
    private PlanetBuildingOverviewDetailedResponse overviewDetailedResponse;

    @Test
    public void createBuildingDetails() {
        given(surfaceWithBuilding.getBuilding()).willReturn(building);
        given(building.getDataId()).willReturn(DATA_ID);
        given(buildingDetailMapper.createBuildingDetail(DATA_ID, Arrays.asList(building))).willReturn(overviewDetailedResponse);

        List<PlanetBuildingOverviewDetailedResponse> result = underTest.createBuildingDetails(Arrays.asList(surfaceWithBuilding, surfaceWithoutBuilding));

        assertThat(result).containsExactly(overviewDetailedResponse);
    }
}