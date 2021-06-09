package com.github.saphyra.apphub.service.skyxplore.game.service.creation;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class GameCreationSettingsValidator {
    void validate(SkyXploreGameCreationSettingsRequest settings) {
        if (isNull(settings)) {
            throw ExceptionFactory.invalidParam("settings", "must not be null");
        }

        if (isNull(settings.getUniverseSize())) {
            throw ExceptionFactory.invalidParam("universeSize", "must not be null");
        }

        if (isNull(settings.getSystemAmount())) {
            throw ExceptionFactory.invalidParam("systemAmount", "must not be null");
        }

        if (isNull(settings.getSystemSize())) {
            throw ExceptionFactory.invalidParam("systemSize", "must not be null");
        }

        if (isNull(settings.getPlanetSize())) {
            throw ExceptionFactory.invalidParam("planetSize", "must not be null");
        }

        if (isNull(settings.getAiPresence())) {
            throw ExceptionFactory.invalidParam("aiPresence", "must not be null");
        }
    }
}
