package com.github.saphyra.apphub.service.skyxplore.game.creation;

import static java.util.Objects.isNull;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class GameCreationSettingsValidator {
    void validate(SkyXploreGameCreationSettingsRequest settings) {
        if (isNull(settings)) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "settings", "must not be null"), "Settings must not be null");
        }

        if (isNull(settings.getUniverseSize())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "universeSize", "must not be null"), "UniverseSize must not be null");
        }

        if (isNull(settings.getSystemAmount())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "systemAmount", "must not be null"), "SystemAmount must not be null");
        }

        if (isNull(settings.getSystemSize())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "systemSize", "must not be null"), "SystemSize must not be null");
        }

        if (isNull(settings.getPlanetSize())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "planetSize", "must not be null"), "PlanetSize must not be null");
        }

        if (isNull(settings.getAiPresence())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "aiPresence", "must not be null"), "AiPresence must not be null");
        }
    }
}
