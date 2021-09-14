package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemSize;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SolarSystemNames;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.GameCreationProperties;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.planet.SystemPopulationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SolarSystemPlacementServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final String SOLAR_SYSTEM_NAME = "solar-system-name";
    private static final String NEW_SYSTEM_NAME = "new-system-name";
    private static final Integer MIN_SYSTEM_SIZE = 324;
    private static final Integer MAX_SYSTEM_SIZE = 640;
    private static final Integer SOLAR_SYSTEM_RADIUS = 345;
    private static final int PLANETS_TO_GENERATE = 3;
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private Random random;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private SolarSystemNames solarSystemNames;

    @Mock
    private SystemPopulationService systemPopulationService;

    @Mock
    private SolarSystemFactory solarSystemFactory;

    @Mock
    private GameCreationProperties gameCreationProperties;

    @InjectMocks
    private SolarSystemPlacementService underTest;

    @Mock
    private SkyXploreGameCreationSettingsRequest settings;

    @Mock
    private SolarSystem existingSolarSystem;

    @Mock
    private GameCreationProperties.SolarSystemProperties solarSystemProperties;

    @Mock
    private Planet planet;

    @Mock
    private SolarSystem newSolarSystem;

    @Test
    public void placeSolarSystem() {
        given(idGenerator.randomUuid()).willReturn(SOLAR_SYSTEM_ID);
        given(existingSolarSystem.getDefaultName()).willReturn(SOLAR_SYSTEM_NAME);
        given(solarSystemNames.getRandomStarName(Arrays.asList(SOLAR_SYSTEM_NAME))).willReturn(NEW_SYSTEM_NAME);
        given(gameCreationProperties.getSolarSystem()).willReturn(solarSystemProperties);
        given(settings.getSystemSize()).willReturn(SystemSize.SMALL);
        given(solarSystemProperties.getRadius()).willReturn(CollectionUtils.singleValueMap(SystemSize.SMALL, new Range<>(MIN_SYSTEM_SIZE, MAX_SYSTEM_SIZE)));
        given(random.randInt(MIN_SYSTEM_SIZE, MAX_SYSTEM_SIZE)).willReturn(SOLAR_SYSTEM_RADIUS);
        given(systemPopulationService.populateSystemWithPlanets(GAME_ID, SOLAR_SYSTEM_ID, NEW_SYSTEM_NAME, SOLAR_SYSTEM_RADIUS, PLANETS_TO_GENERATE, settings)).willReturn(CollectionUtils.singleValueMap(PLANET_ID, planet));
        given(solarSystemFactory.create(GAME_ID, SOLAR_SYSTEM_ID, NEW_SYSTEM_NAME, SOLAR_SYSTEM_RADIUS, Arrays.asList(existingSolarSystem), CollectionUtils.singleValueMap(PLANET_ID, planet))).willReturn(newSolarSystem);

        SolarSystem result = underTest.placeSolarSystem(GAME_ID, settings, Arrays.asList(existingSolarSystem), PLANETS_TO_GENERATE);

        assertThat(result).isEqualTo(newSolarSystem);
    }
}