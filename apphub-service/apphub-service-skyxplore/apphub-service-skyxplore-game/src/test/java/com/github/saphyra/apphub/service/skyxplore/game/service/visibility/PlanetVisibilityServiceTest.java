package com.github.saphyra.apphub.service.skyxplore.game.service.visibility;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class PlanetVisibilityServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @InjectMocks
    private PlanetVisibilityService underTest;

    @Mock
    private Planet planet;

    @Test
    public void notVisiblePlanet() {
        given(planet.getOwner()).willReturn(UUID.randomUUID());

        assertThat(underTest.isVisible(USER_ID, planet)).isFalse();
    }

    @Test
    public void visiblePlanet() {
        given(planet.getOwner()).willReturn(USER_ID);

        assertThat(underTest.isVisible(USER_ID, planet)).isTrue();
    }
}