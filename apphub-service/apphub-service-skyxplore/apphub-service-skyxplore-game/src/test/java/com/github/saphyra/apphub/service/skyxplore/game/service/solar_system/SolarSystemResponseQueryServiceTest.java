package com.github.saphyra.apphub.service.skyxplore.game.service.solar_system;

import com.github.saphyra.apphub.api.skyxplore.response.game.solar_system.PlanetLocationResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.solar_system.SolarSystemResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SolarSystemResponseQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final String DEFAULT_NAME = "default-name";
    private static final Integer RADIUS = 235;

    @Mock
    private GameDao gameDao;

    @Mock
    private PlanetToLocationResponseConverter planetToLocationResponseConverter;

    @InjectMocks
    private SolarSystemResponseQueryService underTest;

    @Mock
    private Game game;

    @Mock
    private Universe universe;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private Planet planet;

    @Mock
    private PlanetLocationResponse planetLocationResponse;

    @Test
    public void getSolarSystem() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.getSystems()).willReturn(CollectionUtils.singleValueMap(new Coordinate(0, 0), solarSystem));
        given(solarSystem.getSolarSystemId()).willReturn(SOLAR_SYSTEM_ID);
        given(solarSystem.getDefaultName()).willReturn(DEFAULT_NAME);
        given(solarSystem.getRadius()).willReturn(RADIUS);
        given(solarSystem.getPlanets()).willReturn(CollectionUtils.singleValueMap(UUID.randomUUID(), planet));
        given(planetToLocationResponseConverter.mapPlanets(any(), eq(game))).willReturn(Arrays.asList(planetLocationResponse));

        SolarSystemResponse result = underTest.getSolarSystem(USER_ID, SOLAR_SYSTEM_ID);

        assertThat(result.getSolarSystemId()).isEqualTo(SOLAR_SYSTEM_ID);
        assertThat(result.getSystemName()).isEqualTo(DEFAULT_NAME);
        assertThat(result.getRadius()).isEqualTo(RADIUS);
        assertThat(result.getPlanets()).containsExactly(planetLocationResponse);
    }
}