package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class GameModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(GameModel model) {
        gameItemValidator.validate(model);

        if (isNull(model.getHost())) {
            throw ExceptionFactory.invalidParam("host", "must not be null");
        }

        if (isNull(model.getName())) {
            throw ExceptionFactory.invalidParam("name", "must not be null");
        }

        if (isNull(model.getLastPlayed())) {
            throw ExceptionFactory.invalidParam("lastPlayed", "must not be null");
        }

        if (isNull(model.getMarkedForDeletion())) {
            throw ExceptionFactory.invalidParam("markedForDeletion", "must not be null");
        }
    }
}
