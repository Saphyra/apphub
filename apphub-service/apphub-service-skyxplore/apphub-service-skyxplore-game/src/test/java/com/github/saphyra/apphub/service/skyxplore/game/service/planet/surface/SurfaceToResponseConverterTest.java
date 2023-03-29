package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ConstructionResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceBuildingResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.Coordinates;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
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

    @Mock
    private GameData gameData;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Constructions constructions;

    @Mock
    private Buildings buildings;

    @Mock
    private Coordinates coordinates;

    @Test
    public void convert() {
        Surface surface = Surface.builder()
            .surfaceId(SURFACE_ID)
            .surfaceType(SurfaceType.DESERT)
            .build();
        given(gameData.getCoordinates()).willReturn(coordinates);
        given(coordinates.findByReferenceId(SURFACE_ID)).willReturn(coordinate);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.of(construction));
        given(gameData.getBuildings()).willReturn(buildings).willReturn(buildings);
        given(buildings.findBySurfaceId(SURFACE_ID)).willReturn(Optional.of(building));

        given(buildingToResponseConverter.convert(gameData, building)).willReturn(surfaceBuildingResponse);
        given(constructionToResponseConverter.convert(construction)).willReturn(constructionResponse);

        SurfaceResponse result = underTest.convert(gameData, SURFACE_ID);

        assertThat(result.getSurfaceId()).isEqualTo(SURFACE_ID);
        assertThat(result.getCoordinate()).isEqualTo(coordinate);
        assertThat(result.getSurfaceType()).isEqualTo(SurfaceType.DESERT.name());
        assertThat(result.getBuilding()).isEqualTo(surfaceBuildingResponse);
        assertThat(result.getTerraformation()).isEqualTo(constructionResponse);
    }
}