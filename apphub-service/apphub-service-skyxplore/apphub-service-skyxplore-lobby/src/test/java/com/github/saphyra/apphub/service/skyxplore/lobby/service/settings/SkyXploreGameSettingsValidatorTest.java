package com.github.saphyra.apphub.service.skyxplore.lobby.service.settings;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.catchThrowable;

class SkyXploreGameSettingsValidatorTest {
    private final SkyXploreGameSettingsValidator underTest = new SkyXploreGameSettingsValidator();

    @Test
    void valid() {
        underTest.validate(createValid().build());
    }

    @Test
    void nullMaxPlayersPerSolarSystem() {
        SkyXploreGameSettings settings = createValid()
            .maxPlayersPerSolarSystem(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "maxPlayersPerSolarSystem", "must not be null");
    }

    @Test
    void tooLowMaxPlayersPerSolarSystem() {
        SkyXploreGameSettings settings = createValid()
            .maxPlayersPerSolarSystem(0)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "maxPlayersPerSolarSystem", "too low");
    }

    @Test
    void nullAdditionalSolarSystems() {
        SkyXploreGameSettings settings = createValid()
            .additionalSolarSystems(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "additionalSolarSystems", "must not be null");
    }

    @Test
    void tooLowAdditionalSolarSystemsMin() {
        SkyXploreGameSettings settings = createValid()
            .additionalSolarSystems(new Range<>(-1, 5))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "additionalSolarSystems.min", "too low");
    }

    @Test
    void tooHighAdditionalSolarSystemsMax() {
        SkyXploreGameSettings settings = createValid()
            .additionalSolarSystems(new Range<>(0, 31))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "additionalSolarSystems.max", "too high");
    }

    @Test
    void additionalSolarSystemsMaxLowerThanMin() {
        SkyXploreGameSettings settings = createValid()
            .additionalSolarSystems(new Range<>(5, 4))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "additionalSolarSystems.max", "too low");
    }

    @Test
    void nullPlanetsPerSolarSystem() {
        SkyXploreGameSettings settings = createValid()
            .planetsPerSolarSystem(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "planetsPerSolarSystem", "must not be null");
    }

    @Test
    void tooLowPlanetsPerSolarSystemMin() {
        SkyXploreGameSettings settings = createValid()
            .planetsPerSolarSystem(new Range<>(-1, 10))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "planetsPerSolarSystem.min", "too low");
    }

    @Test
    void tooHighPlanetsPerSolarSystemMax() {
        SkyXploreGameSettings settings = createValid()
            .planetsPerSolarSystem(new Range<>(1, 11))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "planetsPerSolarSystem.max", "too high");
    }

    @Test
    void planetsPerSolarSystemMaxLowerThanMin() {
        SkyXploreGameSettings settings = createValid()
            .planetsPerSolarSystem(new Range<>(5, 4))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "planetsPerSolarSystem.max", "too low");
    }

    @Test
    void nullPlanetSize() {
        SkyXploreGameSettings settings = createValid()
            .planetSize(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "planetSize", "must not be null");
    }

    @Test
    void tooLowPlanetSizeMin() {
        SkyXploreGameSettings settings = createValid()
            .planetSize(new Range<>(9, 10))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "planetSize.min", "too low");
    }

    @Test
    void tooHighPlanetSizeMax() {
        SkyXploreGameSettings settings = createValid()
            .planetSize(new Range<>(15, 21))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "planetSize.max", "too high");
    }

    @Test
    void planetSizeMaxLowerThanMin() {
        SkyXploreGameSettings settings = createValid()
            .planetSize(new Range<>(15, 14))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "planetSize.max", "too low");
    }

    private static SkyXploreGameSettings.SkyXploreGameSettingsBuilder createValid() {
        return SkyXploreGameSettings.builder()
            .maxPlayersPerSolarSystem(4)
            .additionalSolarSystems(new Range<>(1, 2))
            .planetsPerSolarSystem(new Range<>(3, 4))
            .planetSize(new Range<>(10, 15));
    }
}