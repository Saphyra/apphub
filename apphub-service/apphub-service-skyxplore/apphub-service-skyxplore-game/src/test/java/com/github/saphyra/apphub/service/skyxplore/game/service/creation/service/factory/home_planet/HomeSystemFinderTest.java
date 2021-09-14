package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class HomeSystemFinderTest {
    private static final UUID PLAYER = UUID.randomUUID();

    @Mock
    private FreePlanetCounter freePlanetCounter;

    @Mock
    private InhabitantFinder inhabitantFinder;

    @InjectMocks
    private HomeSystemFinder underTest;

    @Mock
    private SolarSystem solarSystem1;

    @Mock
    private SolarSystem solarSystem2;

    @Test
    public void findHomeSystem_found() {
        given(inhabitantFinder.getInhabitants(solarSystem1)).willReturn(Arrays.asList(PLAYER));
        given(inhabitantFinder.getInhabitants(solarSystem2)).willReturn(Arrays.asList(PLAYER));
        given(freePlanetCounter.getNumberOfFreePlanets(solarSystem1)).willReturn(1L);
        given(freePlanetCounter.getNumberOfFreePlanets(solarSystem2)).willReturn(2L);

        Optional<SolarSystem> result = underTest.findHomeSystem(Arrays.asList(PLAYER), Arrays.asList(solarSystem1, solarSystem2));

        assertThat(result).contains(solarSystem2);
    }

    @Test
    public void findHomeSystem_notFound() {
        given(inhabitantFinder.getInhabitants(solarSystem1)).willReturn(Collections.emptyList());
        given(inhabitantFinder.getInhabitants(solarSystem2)).willReturn(Collections.emptyList());

        Optional<SolarSystem> result = underTest.findHomeSystem(Arrays.asList(PLAYER), Arrays.asList(solarSystem1, solarSystem2));

        assertThat(result).isEmpty();
    }
}