package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.surface;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.common.CoordinateModelFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.surface.SurfaceMapFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SurfaceFactoryTest {
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final int PLANET_SIZE = 435;
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private CoordinateModelFactory coordinateModelFactory;

    @Mock
    private SurfaceMapFactory surfaceMapFactory;

    @InjectMocks
    private SurfaceFactory underTest;

    @Mock
    private CoordinateModel coordinateModel;

    @Test
    public void create() {
        SurfaceType[][] surfaceMap = new SurfaceType[1][1];
        surfaceMap[0] = new SurfaceType[1];
        surfaceMap[0][0] = SurfaceType.CONCRETE;
        Coordinate coordinate = new Coordinate(0, 0);

        given(surfaceMapFactory.createSurfaceMap(PLANET_SIZE)).willReturn(surfaceMap);
        given(coordinateModelFactory.create(coordinate, GAME_ID, SURFACE_ID)).willReturn(coordinateModel);

        given(idGenerator.randomUuid()).willReturn(SURFACE_ID);

        Map<Coordinate, Surface> result = underTest.create(GAME_ID, PLANET_ID, PLANET_SIZE);

        assertThat(result).hasSize(1);
        assertThat(result.get(coordinate).getSurfaceId()).isEqualTo(SURFACE_ID);
        assertThat(result.get(coordinate).getPlanetId()).isEqualTo(PLANET_ID);
        assertThat(result.get(coordinate).getCoordinate()).isEqualTo(coordinateModel);
        assertThat(result.get(coordinate).getSurfaceType()).isEqualTo(SurfaceType.CONCRETE);
    }
}