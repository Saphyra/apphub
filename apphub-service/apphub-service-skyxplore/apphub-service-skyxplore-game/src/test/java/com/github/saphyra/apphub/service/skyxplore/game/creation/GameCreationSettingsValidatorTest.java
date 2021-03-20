package com.github.saphyra.apphub.service.skyxplore.game.creation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.AiPresence;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.PlanetSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.UniverseSize;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;

@RunWith(MockitoJUnitRunner.class)
public class GameCreationSettingsValidatorTest {
    @InjectMocks
    private GameCreationSettingsValidator underTest;

    @Test
    public void nullSettings() {
        Throwable ex = catchThrowable(() -> underTest.validate(null));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("settings", "must not be null");
    }

    @Test
    public void nullUniverseSize() {
        SkyXploreGameCreationSettingsRequest request = validSettings()
            .toBuilder()
            .universeSize(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("universeSize", "must not be null");
    }

    @Test
    public void nullSystemAmount() {
        SkyXploreGameCreationSettingsRequest request = validSettings()
            .toBuilder()
            .systemAmount(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("systemAmount", "must not be null");
    }

    @Test
    public void nullSystemSize() {
        SkyXploreGameCreationSettingsRequest request = validSettings()
            .toBuilder()
            .systemSize(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("systemSize", "must not be null");
    }

    @Test
    public void nullPlanetSize() {
        SkyXploreGameCreationSettingsRequest request = validSettings()
            .toBuilder()
            .planetSize(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("planetSize", "must not be null");
    }

    @Test
    public void nullAiPresence() {
        SkyXploreGameCreationSettingsRequest request = validSettings()
            .toBuilder()
            .aiPresence(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("aiPresence", "must not be null");
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