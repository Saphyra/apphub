package com.github.saphyra.apphub.service.skyxplore.game.service.visibility;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SolarSystemVisibilityServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private PlanetVisibilityService planetVisibilityService;

    @InjectMocks
    private SolarSystemVisibilityService underTest;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private Planet planet;

    @Before
    public void setUp() {
        given(solarSystem.getPlanets()).willReturn(CollectionUtils.singleValueMap(UUID.randomUUID(), planet));
    }

    @Test
    public void visible() {
        given(planetVisibilityService.isVisible(USER_ID, planet)).willReturn(true);

        assertThat(underTest.isVisible(USER_ID, solarSystem)).isTrue();
    }

    @Test
    public void notVisible() {
        given(planetVisibilityService.isVisible(USER_ID, planet)).willReturn(false);

        assertThat(underTest.isVisible(USER_ID, solarSystem)).isFalse();
    }
}