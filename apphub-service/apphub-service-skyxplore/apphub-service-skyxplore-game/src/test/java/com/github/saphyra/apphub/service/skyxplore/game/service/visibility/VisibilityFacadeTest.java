package com.github.saphyra.apphub.service.skyxplore.game.service.visibility;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class VisibilityFacadeTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private SolarSystemVisibilityService solarSystemVisibilityService;

    @Mock
    private PlanetVisibilityService planetVisibilityService;

    @InjectMocks
    private VisibilityFacade underTest;

    @Mock
    private Planet planet;

    @Mock
    private SolarSystem solarSystem;

    @Test
    public void isSolarSystemVisible() {
        given(solarSystemVisibilityService.isVisible(USER_ID, solarSystem)).willReturn(true);

        assertThat(underTest.isVisible(USER_ID, solarSystem)).isTrue();
    }

    @Test
    public void isPlanetVisible() {
        given(planetVisibilityService.isVisible(USER_ID, planet)).willReturn(true);

        assertThat(underTest.isVisible(USER_ID, planet)).isTrue();
    }
}