package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameItemValidator {
    public void validate(GameItem gameItem) {
        if (isNull(gameItem.getId())) {
            throw ExceptionFactory.invalidParam("id", "must not be null");
        }

        validateWithoutId(gameItem);
    }

    public void validateWithoutId(GameItem gameItem) {
        if (isNull(gameItem.getGameId())) {
            throw ExceptionFactory.invalidParam("gameId", "must not be null");
        }
    }
}
