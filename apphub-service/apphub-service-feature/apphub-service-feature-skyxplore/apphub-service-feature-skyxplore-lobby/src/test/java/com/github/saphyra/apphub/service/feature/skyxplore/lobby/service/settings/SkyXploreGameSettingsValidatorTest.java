package com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.settings;

import com.github.saphyra.apphub.api.feature.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
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

        ExceptionValidator.validateInvalidParam(ex, "maxPlayersPerSolarSystem", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void tooLowMaxPlayersPerSolarSystem() {
        SkyXploreGameSettings settings = createValid()
            .maxPlayersPerSolarSystem(0)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "maxPlayersPerSolarSystem", ValidationUtil.TOO_LOW);
    }

    @Test
    void nullAdditionalSolarSystems() {
        SkyXploreGameSettings settings = createValid()
            .additionalSolarSystems(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "additionalSolarSystems", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void tooLowAdditionalSolarSystemsMin() {
        SkyXploreGameSettings settings = createValid()
            .additionalSolarSystems(new Range<>(-1, 5))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "additionalSolarSystems.min", ValidationUtil.TOO_LOW);
    }

    @Test
    void tooHighAdditionalSolarSystemsMax() {
        SkyXploreGameSettings settings = createValid()
            .additionalSolarSystems(new Range<>(0, 31))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "additionalSolarSystems.max", ValidationUtil.TOO_HIGH);
    }

    @Test
    void additionalSolarSystemsMaxLowerThanMin() {
        SkyXploreGameSettings settings = createValid()
            .additionalSolarSystems(new Range<>(5, 4))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "additionalSolarSystems.max", ValidationUtil.TOO_LOW);
    }

    @Test
    void nullPlanetsPerSolarSystem() {
        SkyXploreGameSettings settings = createValid()
            .planetsPerSolarSystem(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "planetsPerSolarSystem", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void tooLowPlanetsPerSolarSystemMin() {
        SkyXploreGameSettings settings = createValid()
            .planetsPerSolarSystem(new Range<>(-1, 10))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "planetsPerSolarSystem.min", ValidationUtil.TOO_LOW);
    }

    @Test
    void tooHighPlanetsPerSolarSystemMax() {
        SkyXploreGameSettings settings = createValid()
            .planetsPerSolarSystem(new Range<>(1, 11))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "planetsPerSolarSystem.max", ValidationUtil.TOO_HIGH);
    }

    @Test
    void planetsPerSolarSystemMaxLowerThanMin() {
        SkyXploreGameSettings settings = createValid()
            .planetsPerSolarSystem(new Range<>(5, 4))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "planetsPerSolarSystem.max", ValidationUtil.TOO_LOW);
    }

    @Test
    void nullPlanetSize() {
        SkyXploreGameSettings settings = createValid()
            .planetSize(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "planetSize", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void tooLowPlanetSizeMin() {
        SkyXploreGameSettings settings = createValid()
            .planetSize(new Range<>(9, 10))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "planetSize.min", ValidationUtil.TOO_LOW);
    }

    @Test
    void tooHighPlanetSizeMax() {
        SkyXploreGameSettings settings = createValid()
            .planetSize(new Range<>(15, 21))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "planetSize.max", ValidationUtil.TOO_HIGH);
    }

    @Test
    void planetSizeMaxLowerThanMin() {
        SkyXploreGameSettings settings = createValid()
            .planetSize(new Range<>(15, 14))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(settings));

        ExceptionValidator.validateInvalidParam(ex, "planetSize.max", ValidationUtil.TOO_LOW);
    }

    private static SkyXploreGameSettings.SkyXploreGameSettingsBuilder createValid() {
        return SkyXploreGameSettings.builder()
            .maxPlayersPerSolarSystem(4)
            .additionalSolarSystems(new Range<>(1, 2))
            .planetsPerSolarSystem(new Range<>(3, 4))
            .planetSize(new Range<>(10, 15));
    }
}