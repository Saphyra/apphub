package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ConstructionResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceBuildingResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SurfaceToResponseConverterTest {
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private BuildingToResponseConverter buildingToResponseConverter;

    @Mock
    private ConstructionToResponseConverter constructionToResponseConverter;

    @InjectMocks
    private SurfaceToResponseConverter underTest;

    @Mock
    private Building building;

    @Mock
    private SurfaceBuildingResponse surfaceBuildingResponse;

    @Mock
    private CoordinateModel coordinateModel;

    @Mock
    private Coordinate coordinate;

    @Mock
    private Construction construction;

    @Mock
    private ConstructionResponse constructionResponse;

    @Test
    public void convert() {
        Surface surface = Surface.builder()
            .surfaceId(SURFACE_ID)
            .coordinate(coordinateModel)
            .surfaceType(SurfaceType.DESERT)
            .building(building)
            .terraformation(construction)
            .build();
        given(buildingToResponseConverter.convert(building)).willReturn(surfaceBuildingResponse);
        given(constructionToResponseConverter.convert(construction)).willReturn(constructionResponse);
        given(coordinateModel.getCoordinate()).willReturn(coordinate);

        SurfaceResponse result = underTest.convert(surface);

        assertThat(result.getSurfaceId()).isEqualTo(SURFACE_ID);
        assertThat(result.getCoordinate()).isEqualTo(coordinate);
        assertThat(result.getSurfaceType()).isEqualTo(SurfaceType.DESERT.name());
        assertThat(result.getBuilding()).isEqualTo(surfaceBuildingResponse);
        assertThat(result.getTerraformation()).isEqualTo(constructionResponse);
    }
}