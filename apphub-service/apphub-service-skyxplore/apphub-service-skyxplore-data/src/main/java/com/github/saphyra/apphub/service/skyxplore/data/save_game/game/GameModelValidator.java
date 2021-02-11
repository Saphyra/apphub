package com.github.saphyra.apphub.service.skyxplore.data.save_game.game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class GameModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(GameModel model) {
        gameItemValidator.validate(model);

        if (isNull(model.getHost())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "host", "must not be null"), "host must not be null");
        }

        if (isNull(model.getName())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "name", "must not be null"), "name must not be null");
        }
    }
}
