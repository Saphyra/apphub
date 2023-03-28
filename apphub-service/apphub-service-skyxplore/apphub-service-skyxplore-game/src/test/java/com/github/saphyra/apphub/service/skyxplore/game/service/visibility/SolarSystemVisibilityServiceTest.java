package com.github.saphyra.apphub.service.skyxplore.game.service.visibility;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SolarSystemVisibilityServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();

    @Mock
    private PlanetVisibilityService planetVisibilityService;

    @InjectMocks
    private SolarSystemVisibilityService underTest;

    @Mock
    private Planet planet;

    @Mock
    private GameData gameData;

    @Mock
    private Planets planets;

    @Test
    public void visible() {
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.getBySolarSystemId(SOLAR_SYSTEM_ID)).willReturn(List.of(planet));
        given(planetVisibilityService.isVisible(USER_ID, planet)).willReturn(true);

        assertThat(underTest.isVisible(gameData, USER_ID, SOLAR_SYSTEM_ID)).isTrue();
    }

    @Test
    public void notVisible() {
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.getBySolarSystemId(SOLAR_SYSTEM_ID)).willReturn(List.of(planet));
        given(planetVisibilityService.isVisible(USER_ID, planet)).willReturn(false);

        assertThat(underTest.isVisible(gameData, USER_ID, SOLAR_SYSTEM_ID)).isFalse();
    }
}