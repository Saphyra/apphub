package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.apphub.lib.geometry.Coordinate;

@RunWith(MockitoJUnitRunner.class)
public class UniverseTest {
    private static final UUID PLANET_ID = UUID.randomUUID();

    private Universe underTest;

    @Mock
    private Coordinate coordinate;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private Planet planet;

    @Test
    public void findByPlanetIdValidated_notFound() {
        underTest = Universe.builder()
            .systems(CollectionUtils.singleValueMap(coordinate, solarSystem))
            .build();
        given(solarSystem.getPlanets()).willReturn(CollectionUtils.singleValueMap(UUID.randomUUID(), planet));
        given(planet.getPlanetId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.findPlanetByIdValidated(UUID.randomUUID()));

        assertThat(ex).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void findByPlanetIdValidated() {
        underTest = Universe.builder()
            .systems(CollectionUtils.singleValueMap(coordinate, solarSystem))
            .build();
        given(solarSystem.getPlanets()).willReturn(CollectionUtils.singleValueMap(PLANET_ID, planet));
        given(planet.getPlanetId()).willReturn(PLANET_ID);

        Planet result = underTest.findPlanetByIdValidated(PLANET_ID);

        assertThat(result).isEqualTo(planet);
    }
}