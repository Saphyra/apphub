package com.github.saphyra.apphub.service.skyxplore.game.service.visibility;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class VisibilityFacadeTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();

    @Mock
    private SolarSystemVisibilityService solarSystemVisibilityService;

    @Mock
    private PlanetVisibilityService planetVisibilityService;

    @InjectMocks
    private VisibilityFacade underTest;

    @Mock
    private Planet planet;

    @Mock
    private GameData gameData;

    @Test
    public void isSolarSystemVisible() {
        given(solarSystemVisibilityService.isVisible(gameData, USER_ID, SOLAR_SYSTEM_ID)).willReturn(true);

        assertThat(underTest.isVisible(gameData, USER_ID, SOLAR_SYSTEM_ID)).isTrue();
    }

    @Test
    public void isPlanetVisible() {
        given(planetVisibilityService.isVisible(USER_ID, planet)).willReturn(true);

        assertThat(underTest.isVisible(USER_ID, planet)).isTrue();
    }
}