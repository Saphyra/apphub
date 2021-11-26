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

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UniverseTest {
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID OWNER = UUID.randomUUID();

    private Universe underTest;

    @Mock
    private Coordinate coordinate;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private Planet planet;

    @Test
    public void findSolarSystemByIdValidated_notFound() {
        underTest = Universe.builder()
            .systems(Collections.emptyMap())
            .build();

        Throwable ex = catchThrowable(() -> underTest.findSolarSystemByIdValidated(SOLAR_SYSTEM_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void findSolarSystemByIdValidated() {
        underTest = Universe.builder()
            .systems(CollectionUtils.singleValueMap(coordinate, solarSystem))
            .build();
        given(solarSystem.getSolarSystemId()).willReturn(SOLAR_SYSTEM_ID);

        SolarSystem result = underTest.findSolarSystemByIdValidated(SOLAR_SYSTEM_ID);

        assertThat(result).isEqualTo(solarSystem);
    }

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

    @Test
    public void findPlanetByIdAndOwnerValidated_notOwner() {
        underTest = Universe.builder()
            .systems(CollectionUtils.singleValueMap(coordinate, solarSystem))
            .build();
        given(solarSystem.getPlanets()).willReturn(CollectionUtils.singleValueMap(PLANET_ID, planet));
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(planet.getOwner()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.findPlanetByIdAndOwnerValidated(OWNER, PLANET_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void findPlanetByIdAndOwnerValidated() {
        underTest = Universe.builder()
            .systems(CollectionUtils.singleValueMap(coordinate, solarSystem))
            .build();
        given(solarSystem.getPlanets()).willReturn(CollectionUtils.singleValueMap(PLANET_ID, planet));
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(planet.getOwner()).willReturn(OWNER);

        Planet result = underTest.findPlanetByIdAndOwnerValidated(OWNER, PLANET_ID);

        assertThat(result).isEqualTo(planet);
    }
}