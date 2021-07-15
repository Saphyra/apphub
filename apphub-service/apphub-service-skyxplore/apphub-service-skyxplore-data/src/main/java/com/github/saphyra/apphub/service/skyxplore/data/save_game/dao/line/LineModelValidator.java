package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.line;

import com.github.saphyra.apphub.api.skyxplore.model.game.LineModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class LineModelValidator {
    private final GameItemValidator gameItemValidator;

    void validate(LineModel model) {
        gameItemValidator.validate(model);

        if (isNull(model.getReferenceId())) {
            throw ExceptionFactory.invalidParam("referenceId", "must not be null");
        }

        if (isNull(model.getA())) {
            throw ExceptionFactory.invalidParam("a", "must not be null");
        }

        if (isNull(model.getB())) {
            throw ExceptionFactory.invalidParam("b", "must not be null");
        }
    }
}
