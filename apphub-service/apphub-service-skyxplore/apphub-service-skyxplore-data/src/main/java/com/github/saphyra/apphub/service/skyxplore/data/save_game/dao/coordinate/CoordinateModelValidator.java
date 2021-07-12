package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.coordinate;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class CoordinateModelValidator {
    private final GameItemValidator gameItemValidator;

    void validate(CoordinateModel model) {
        gameItemValidator.validate(model);

        if (isNull(model.getReferenceId())) {
            throw ExceptionFactory.invalidParam("referenceId", "must not be null");
        }

        if (isNull(model.getCoordinate())) {
            throw ExceptionFactory.invalidParam("coordinate", "must not be null");
        }

        if (isNull(model.getCoordinate().getX())) {
            throw ExceptionFactory.invalidParam("coordinate.x", "must not be null");
        }

        if (isNull(model.getCoordinate().getY())) {
            throw ExceptionFactory.invalidParam("coordinate.y", "must not be null");
        }
    }
}
