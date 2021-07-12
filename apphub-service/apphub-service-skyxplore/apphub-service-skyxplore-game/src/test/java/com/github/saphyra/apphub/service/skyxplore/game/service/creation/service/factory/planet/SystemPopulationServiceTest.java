package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.planet;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.PlanetSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemSize;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.GameCreationProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SystemPopulationServiceTest {
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final String SYSTEM_NAME = "system-name";
    private static final int SYSTEM_RADIUS = 43536;
    private static final Integer MIN_PLANET_AMOUNT = 425;
    private static final Integer MAX_PLANET_AMOUNT = 356254;
    private static final Integer MIN_PLANET_SIZE = 3465253;
    private static final Integer MAX_PLANET_SIZE = 64698;
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private Random random;

    @Mock
    private GameCreationProperties properties;

    @Mock
    private PlanetCoordinateProvider coordinateProvider;

    private final ExecutorServiceBean executorServiceBean = new ExecutorServiceBean(new SleepService());

    @Mock
    private PlanetFactory planetFactory;

    private SystemPopulationService underTest;

    @Mock
    private SkyXploreGameCreationSettingsRequest settings;

    @Mock
    private GameCreationProperties.PlanetProperties planetProperties;

    @Mock
    private Coordinate coordinate;

    @Mock
    private Planet planet;

    @Before
    public void setUp() {
        underTest = SystemPopulationService.builder()
            .random(random)
            .properties(properties)
            .coordinateProvider(coordinateProvider)
            .executorServiceBean(executorServiceBean)
            .planetFactory(planetFactory)
            .build();
    }

    @Test
    public void create() {
        given(settings.getSystemSize()).willReturn(SystemSize.LARGE);
        given(settings.getPlanetSize()).willReturn(PlanetSize.LARGE);
        given(properties.getPlanet()).willReturn(planetProperties);
        given(planetProperties.getSystemSize()).willReturn(CollectionUtils.singleValueMap(SystemSize.LARGE, new Range<>(MIN_PLANET_AMOUNT, MAX_PLANET_AMOUNT)));
        Range<Integer> planetSizeRange = new Range<>(MIN_PLANET_SIZE, MAX_PLANET_SIZE);
        given(planetProperties.getPlanetSize()).willReturn(CollectionUtils.singleValueMap(PlanetSize.LARGE, planetSizeRange));
        given(random.randInt(MIN_PLANET_AMOUNT, MAX_PLANET_AMOUNT)).willReturn(1);
        given(coordinateProvider.getCoordinates(1, SYSTEM_RADIUS)).willReturn(Arrays.asList(coordinate));
        given(planetFactory.create(GAME_ID, 0, coordinate, SOLAR_SYSTEM_ID, SYSTEM_NAME, planetSizeRange)).willReturn(planet);
        given(planet.getPlanetId()).willReturn(PLANET_ID);

        Map<UUID, Planet> result = underTest.populateSystemWithPlanets(GAME_ID, SOLAR_SYSTEM_ID, SYSTEM_NAME, SYSTEM_RADIUS, settings);

        assertThat(result).containsEntry(PLANET_ID, planet);
    }
}