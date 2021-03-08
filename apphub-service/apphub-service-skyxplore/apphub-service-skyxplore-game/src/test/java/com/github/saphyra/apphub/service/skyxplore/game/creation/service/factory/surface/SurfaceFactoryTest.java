package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.surface;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SurfaceFactoryTest {
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final int PLANET_SIZE = 435;
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private SurfaceMapFactory surfaceMapFactory;

    @InjectMocks
    private SurfaceFactory underTest;

    @Test
    public void create() {
        SurfaceType[][] surfaceMap = new SurfaceType[1][1];
        surfaceMap[0] = new SurfaceType[1];
        surfaceMap[0][0] = SurfaceType.CONCRETE;

        given(surfaceMapFactory.createSurfaceMap(PLANET_SIZE)).willReturn(surfaceMap);

        given(idGenerator.randomUuid()).willReturn(SURFACE_ID);

        Map<Coordinate, Surface> result = underTest.create(PLANET_ID, PLANET_SIZE);

        Coordinate coordinate = new Coordinate(0, 0);
        assertThat(result).hasSize(1);
        assertThat(result.get(coordinate).getSurfaceId()).isEqualTo(SURFACE_ID);
        assertThat(result.get(coordinate).getPlanetId()).isEqualTo(PLANET_ID);
        assertThat(result.get(coordinate).getCoordinate()).isEqualTo(coordinate);
        assertThat(result.get(coordinate).getSurfaceType()).isEqualTo(SurfaceType.CONCRETE);
    }
}