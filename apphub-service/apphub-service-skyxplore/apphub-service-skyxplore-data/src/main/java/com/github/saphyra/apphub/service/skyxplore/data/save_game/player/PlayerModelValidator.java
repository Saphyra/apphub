package com.github.saphyra.apphub.service.skyxplore.data.save_game.player;

import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlayerModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(PlayerModel model) {
        gameItemValidator.validate(model);

        if (isNull(model.getUserId())) {
            throw ExceptionFactory.invalidParam("userId", "must not be null");
        }

        if (isNull(model.getUsername())) {
            throw ExceptionFactory.invalidParam("username", "must not be null");
        }

        if (isNull(model.getAi())) {
            throw ExceptionFactory.invalidParam("ai", "must not be null");
        }
    }
}
