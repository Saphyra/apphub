package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemSize;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.PlanetProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SolarSystemGeneratorServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final int PLAYER_COUNT = 4;
    private static final Integer MIN_PLANETS_PER_SYSTEM = 324;
    private static final Integer MAX_PLANETS_PER_SYSTEM = 354;

    @Mock
    private GameProperties gameCreationProperties;

    @Mock
    private Random random;

    @Mock
    private SolarSystemPlacementService solarSystemPlacementService;

    @Mock
    private SolarSystemCoordinateShifter solarSystemCoordinateShifter;

    @Mock
    private ExpectedPlanetCountCalculator expectedPlanetCountCalculator;

    @InjectMocks
    private SolarSystemGeneratorService underTest;

    @Mock
    private SkyXploreGameCreationSettingsRequest settings;

    @Mock
    private PlanetProperties planetProperties;

    @Mock
    private SolarSystem solarSystem;

    @Test
    public void generateSolarSystems() {
        given(settings.getSystemSize()).willReturn(SystemSize.SMALL);
        given(gameCreationProperties.getPlanet()).willReturn(planetProperties);
        given(planetProperties.getPlanetsPerSystem()).willReturn(CollectionUtils.singleValueMap(SystemSize.SMALL, new Range<>(MIN_PLANETS_PER_SYSTEM, MAX_PLANETS_PER_SYSTEM)));

        given(expectedPlanetCountCalculator.calculateExpectedPlanetCount(PLAYER_COUNT, settings)).willReturn(1);
        given(random.randInt(MIN_PLANETS_PER_SYSTEM, MAX_PLANETS_PER_SYSTEM)).willReturn(134);

        given(solarSystemPlacementService.placeSolarSystem(GAME_ID, settings, new ArrayList<>(), 1)).willReturn(solarSystem);

        List<SolarSystem> result = underTest.generateSolarSystems(GAME_ID, PLAYER_COUNT, settings);

        verify(solarSystemCoordinateShifter).shiftCoordinates(result);

        assertThat(result).containsExactly(solarSystem);
    }
}