package com.github.saphyra.apphub.service.skyxplore.game.service.creation;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.AiPresence;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.PlanetSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.UniverseSize;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
public class GameCreationSettingsValidatorTest {
    @InjectMocks
    private GameCreationSettingsValidator underTest;

    @Test
    public void nullSettings() {
        Throwable ex = catchThrowable(() -> underTest.validate(null));

        ExceptionValidator.validateInvalidParam(ex, "settings", "must not be null");
    }

    @Test
    public void nullUniverseSize() {
        SkyXploreGameCreationSettingsRequest request = validSettings()
            .toBuilder()
            .universeSize(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "universeSize", "must not be null");
    }

    @Test
    public void nullSystemAmount() {
        SkyXploreGameCreationSettingsRequest request = validSettings()
            .toBuilder()
            .systemAmount(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "systemAmount", "must not be null");
    }

    @Test
    public void nullSystemSize() {
        SkyXploreGameCreationSettingsRequest request = validSettings()
            .toBuilder()
            .systemSize(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "systemSize", "must not be null");
    }

    @Test
    public void nullPlanetSize() {
        SkyXploreGameCreationSettingsRequest request = validSettings()
            .toBuilder()
            .planetSize(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "planetSize", "must not be null");
    }

    @Test
    public void nullAiPresence() {
        SkyXploreGameCreationSettingsRequest request = validSettings()
            .toBuilder()
            .aiPresence(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "aiPresence", "must not be null");
    }

    @Test
    public void valid() {
        underTest.validate(validSettings());
    }

    private SkyXploreGameCreationSettingsRequest validSettings() {
        return SkyXploreGameCreationSettingsRequest.builder()
            .universeSize(UniverseSize.SMALL)
            .systemAmount(SystemAmount.RANDOM)
            .systemSize(SystemSize.SMALL)
            .planetSize(PlanetSize.LARGE)
            .aiPresence(AiPresence.COMMON)
            .build();
    }
}