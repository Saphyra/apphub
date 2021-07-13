package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SurfaceLoaderTest {
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private GameItemLoader gameItemLoader;

    @Mock
    private CoordinateLoader coordinateLoader;

    @Mock
    private BuildingLoader buildingLoader;

    @InjectMocks
    private SurfaceLoader underTest;

    @Mock
    private SurfaceModel surfaceModel;

    @Mock
    private CoordinateModel coordinateModel;

    @Mock
    private Building building;

    @Mock
    private Coordinate coordinate;

    @Test
    public void load() {
        given(gameItemLoader.loadChildren(PLANET_ID, GameItemType.SURFACE, SurfaceModel[].class)).willReturn(Arrays.asList(surfaceModel));

        given(surfaceModel.getId()).willReturn(SURFACE_ID);
        given(surfaceModel.getPlanetId()).willReturn(PLANET_ID);
        given(coordinateLoader.loadOneByReferenceId(SURFACE_ID)).willReturn(coordinateModel);
        given(surfaceModel.getSurfaceType()).willReturn(SurfaceType.CONCRETE.name());
        given(buildingLoader.load(SURFACE_ID)).willReturn(building);
        given(coordinateModel.getCoordinate()).willReturn(coordinate);

        Map<Coordinate, Surface> result = underTest.load(PLANET_ID);

        assertThat(result).hasSize(1);
        Surface surface = result.get(coordinate);
        assertThat(surface.getSurfaceId()).isEqualTo(SURFACE_ID);
        assertThat(surface.getPlanetId()).isEqualTo(PLANET_ID);
        assertThat(surface.getCoordinate()).isEqualTo(coordinateModel);
        assertThat(surface.getSurfaceType()).isEqualTo(SurfaceType.CONCRETE);
        assertThat(surface.getBuilding()).isEqualTo(building);
    }
}