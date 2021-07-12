package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreLoadGameRequest;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class LoadGameRequestValidator {
    void validate(SkyXploreLoadGameRequest request) {
        if (isNull(request.getHost())) {
            throw ExceptionFactory.invalidParam("host", "must not be null");
        }

        if (isNull(request.getGameId())) {
            throw ExceptionFactory.invalidParam("gameId", "must not be null");
        }

        if (isNull(request.getMembers())) {
            throw ExceptionFactory.invalidParam("members", "must not be null");
        }

        if (!request.getMembers().contains(request.getHost())) {
            throw ExceptionFactory.invalidParam("members", "does not contain host");
        }
    }
}
