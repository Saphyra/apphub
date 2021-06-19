package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceBuildingResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SurfaceToResponseConverterTest {
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final Coordinate COORDINATE = new Coordinate(0, 0);

    @Mock
    private BuildingToResponseConverter buildingToResponseConverter;

    @InjectMocks
    private SurfaceToResponseConverter underTest;

    @Mock
    private Building building;

    @Mock
    private SurfaceBuildingResponse surfaceBuildingResponse;

    @Test
    public void convert() {
        Surface surface = Surface.builder()
            .surfaceId(SURFACE_ID)
            .coordinate(COORDINATE)
            .surfaceType(SurfaceType.DESERT)
            .building(building)
            .build();
        given(buildingToResponseConverter.convert(building)).willReturn(surfaceBuildingResponse);

        SurfaceResponse result = underTest.convert(surface);

        assertThat(result.getSurfaceId()).isEqualTo(SURFACE_ID);
        assertThat(result.getCoordinate()).isEqualTo(COORDINATE);
        assertThat(result.getSurfaceType()).isEqualTo(SurfaceType.DESERT.name());
        assertThat(result.getBuilding()).isEqualTo(surfaceBuildingResponse);
    }
}