package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

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

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
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