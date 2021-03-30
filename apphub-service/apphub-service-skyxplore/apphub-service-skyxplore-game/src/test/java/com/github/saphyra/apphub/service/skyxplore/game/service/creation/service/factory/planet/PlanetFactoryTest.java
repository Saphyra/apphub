package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.planet;

import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.surface.SurfaceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
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
public class PlanetFactoryTest {
    private static final int MIN_PLANET_SIZE_RANGE = 1;
    private static final int MAX_PLANET_SIZE_RANGE = 4;
    private static final int PLANET_SIZE = 3453;
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final Integer PLANET_INDEX = 1;
    private static final String SYSTEM_NAME = "system-name";
    @Mock
    private IdGenerator idGenerator;

    @Mock
    private Random random;

    @Mock
    private SurfaceFactory surfaceFactory;

    @InjectMocks
    private PlanetFactory underTest;

    @Mock
    private Coordinate coordinate;

    @Mock
    private Surface surface;

    @Test
    public void create() {
        given(random.randInt(MIN_PLANET_SIZE_RANGE, MAX_PLANET_SIZE_RANGE)).willReturn(PLANET_SIZE);
        given(idGenerator.randomUuid()).willReturn(PLANET_ID);

        given(surfaceFactory.create(PLANET_ID, PLANET_SIZE)).willReturn(CollectionUtils.singleValueMap(coordinate, surface));

        Planet result = underTest.create(PLANET_INDEX, coordinate, SOLAR_SYSTEM_ID, SYSTEM_NAME, new Range<>(MIN_PLANET_SIZE_RANGE, MAX_PLANET_SIZE_RANGE));

        assertThat(result.getPlanetId()).isEqualTo(PLANET_ID);
        assertThat(result.getSolarSystemId()).isEqualTo(SOLAR_SYSTEM_ID);
        assertThat(result.getCoordinate()).isEqualTo(coordinate);
        assertThat(result.getDefaultName()).isEqualTo(SYSTEM_NAME + " B");
        assertThat(result.getSize()).isEqualTo(PLANET_SIZE);
        assertThat(result.getSurfaces()).containsEntry(coordinate, surface);
    }
}