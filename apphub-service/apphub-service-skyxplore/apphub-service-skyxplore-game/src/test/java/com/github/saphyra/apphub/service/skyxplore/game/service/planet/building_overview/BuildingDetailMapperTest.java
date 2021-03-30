package com.github.saphyra.apphub.service.skyxplore.game.service.planet.building_overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewDetailedResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class BuildingDetailMapperTest {
    private static final String DATA_ID = "data-id";

    @InjectMocks
    private BuildingDetailMapper underTest;

    @Mock
    private Building building1;

    @Mock
    private Building building2;

    @Test
    public void createBuildingDetail() {
        given(building1.getLevel()).willReturn(1);
        given(building2.getLevel()).willReturn(2);

        PlanetBuildingOverviewDetailedResponse result = underTest.createBuildingDetail(DATA_ID, Arrays.asList(building1, building2));

        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getLevelSum()).isEqualTo(3);
        assertThat(result.getUsedSlots()).isEqualTo(2);
    }
}