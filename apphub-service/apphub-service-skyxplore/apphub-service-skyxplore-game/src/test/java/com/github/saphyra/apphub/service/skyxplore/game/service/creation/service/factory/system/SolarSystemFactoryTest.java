package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.common.CoordinateModelFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SolarSystemFactoryTest {
    private static final String SYSTEM_NAME = "system-name";
    private static final Integer RADIUS = 200;
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private CoordinateModelFactory coordinateModelFactory;

    @Mock
    private SolarSystemCoordinateProvider solarSystemCoordinateProvider;

    @InjectMocks
    private SolarSystemFactory underTest;

    @Mock
    private SolarSystem existingSolarSystem;

    @Mock
    private Coordinate coordinate;

    @Mock
    private Planet planet;

    @Mock
    private CoordinateModel solarSystemCoordinateModel;

    @Test
    public void create() {
        given(solarSystemCoordinateProvider.getCoordinate(Arrays.asList(existingSolarSystem))).willReturn(coordinate);
        given(coordinateModelFactory.create(coordinate, GAME_ID, SOLAR_SYSTEM_ID)).willReturn(solarSystemCoordinateModel);

        Map<UUID, Planet> planets = CollectionUtils.singleValueMap(PLANET_ID, planet);
        SolarSystem result = underTest.create(GAME_ID, SOLAR_SYSTEM_ID, SYSTEM_NAME, RADIUS, Arrays.asList(existingSolarSystem), planets);

        assertThat(result.getSolarSystemId()).isEqualTo(SOLAR_SYSTEM_ID);
        assertThat(result.getRadius()).isEqualTo((int) (RADIUS));
        assertThat(result.getDefaultName()).isEqualTo(SYSTEM_NAME);
        assertThat(result.getCoordinate()).isEqualTo(solarSystemCoordinateModel);
        assertThat(result.getPlanets()).isEqualTo(planets);
    }
}