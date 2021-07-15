package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemSize;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.common.CoordinateModelFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.GameCreationProperties;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.planet.SystemPopulationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SolarSystemFactoryTest {
    private static final Integer MIN_RADIUS = 132;
    private static final Integer MAX_RADIUS = 245;
    private static final String SYSTEM_NAME = "system-name";
    private static final Integer RADIUS = 200;
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private SystemPopulationService systemPopulationService;

    @Mock
    private Random random;

    @Mock
    private GameCreationProperties properties;

    @Mock
    private CoordinateModelFactory coordinateModelFactory;

    @InjectMocks
    private SolarSystemFactory underTest;

    @Mock
    private GameCreationProperties.SolarSystemProperties solarSystemProperties;

    @Mock
    private Coordinate coordinate;

    @Mock
    private Planet planet;

    @Mock
    private CoordinateModel coordinateModel;

    @Test
    public void create() {
        given(properties.getSolarSystem()).willReturn(solarSystemProperties);
        given(solarSystemProperties.getRadius()).willReturn(CollectionUtils.singleValueMap(SystemSize.SMALL, new Range<>(MIN_RADIUS, MAX_RADIUS)));
        SkyXploreGameCreationSettingsRequest settings = SkyXploreGameCreationSettingsRequest.builder()
            .systemSize(SystemSize.SMALL)
            .build();
        given(random.randInt(MIN_RADIUS, MAX_RADIUS)).willReturn(RADIUS);
        given(idGenerator.randomUuid()).willReturn(SOLAR_SYSTEM_ID);
        given(systemPopulationService.populateSystemWithPlanets(GAME_ID, SOLAR_SYSTEM_ID, SYSTEM_NAME, RADIUS, settings)).willReturn(CollectionUtils.singleValueMap(PLANET_ID, planet));
        given(coordinateModelFactory.create(coordinate, GAME_ID, SOLAR_SYSTEM_ID)).willReturn(coordinateModel);

        SolarSystem result = underTest.create(GAME_ID, settings, SYSTEM_NAME, coordinate);

        assertThat(result.getSolarSystemId()).isEqualTo(SOLAR_SYSTEM_ID);
        assertThat(result.getRadius()).isEqualTo(RADIUS);
        assertThat(result.getDefaultName()).isEqualTo(SYSTEM_NAME);
        assertThat(result.getCoordinate()).isEqualTo(coordinateModel);
        assertThat(result.getPlanets()).containsEntry(PLANET_ID, planet);
    }
}