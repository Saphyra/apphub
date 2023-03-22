package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.planet;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.common.CoordinateModelFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PlanetFactoryTest {
    private static final int MIN_PLANET_SIZE_RANGE = 1;
    private static final int MAX_PLANET_SIZE_RANGE = 4;
    private static final int PLANET_SIZE = 3453;
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final Integer PLANET_INDEX = 1;
    private static final String SYSTEM_NAME = "system-name";
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private Random random;

    @Mock
    private SurfaceFactory surfaceFactory;

    @Mock
    private StorageDetailsFactory storageDetailsFactory;

    @Mock
    private CoordinateModelFactory coordinateModelFactory;

    @InjectMocks
    private PlanetFactory underTest;

    @Mock
    private Coordinate coordinate;

    @Mock
    private Surface surface;

    @Mock
    private CoordinateModel coordinateModel;

    @Mock
    private StorageDetails storageDetails;

    @Test
    public void create() {
        given(random.randInt(MIN_PLANET_SIZE_RANGE, MAX_PLANET_SIZE_RANGE)).willReturn(PLANET_SIZE);
        given(idGenerator.randomUuid()).willReturn(PLANET_ID);

        given(surfaceFactory.create(GAME_ID, PLANET_ID, PLANET_SIZE)).willReturn(CollectionUtils.singleValueMap(coordinate, surface));
        given(coordinateModelFactory.create(coordinate, GAME_ID, PLANET_ID)).willReturn(coordinateModel);
        given(storageDetailsFactory.create(GAME_ID, PLANET_ID, LocationType.PLANET)).willReturn(storageDetails);

        Planet result = underTest.create(GAME_ID, PLANET_INDEX, coordinate, SOLAR_SYSTEM_ID, SYSTEM_NAME, new Range<>(MIN_PLANET_SIZE_RANGE, MAX_PLANET_SIZE_RANGE));

        assertThat(result.getPlanetId()).isEqualTo(PLANET_ID);
        assertThat(result.getSolarSystemId()).isEqualTo(SOLAR_SYSTEM_ID);
        assertThat(result.getCoordinate()).isEqualTo(coordinateModel);
        assertThat(result.getDefaultName()).isEqualTo(SYSTEM_NAME + " B");
        assertThat(result.getSize()).isEqualTo(PLANET_SIZE);
        assertThat(result.getSurfaces()).containsEntry(coordinate, surface);
        assertThat(result.getStorageDetails()).isEqualTo(storageDetails);
    }
}