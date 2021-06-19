package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceBuildingResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BuildingToResponseConverterTest {
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int LEVEL = 432;

    @InjectMocks
    private BuildingToResponseConverter underTest;

    @Test
    public void convertNull() {
        assertThat(underTest.convert(null)).isNull();
    }

    @Test
    public void convert() {
        Building building = Building.builder()
            .buildingId(BUILDING_ID)
            .dataId(DATA_ID)
            .level(LEVEL)
            .build();

        SurfaceBuildingResponse result = underTest.convert(building);

        assertThat(result.getBuildingId()).isEqualTo(BUILDING_ID);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getLevel()).isEqualTo(LEVEL);
    }
}