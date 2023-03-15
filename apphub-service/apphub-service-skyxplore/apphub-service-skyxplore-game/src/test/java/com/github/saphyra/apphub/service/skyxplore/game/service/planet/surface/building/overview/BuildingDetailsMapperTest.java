package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewDetailedResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
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