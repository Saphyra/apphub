package com.github.saphyra.apphub.service.skyxplore.data.save_game.surface;

import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
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
public class SurfaceModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(SurfaceModel model) {
        gameItemValidator.validate(model);

        if (isNull(model.getPlanetId())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "planetId", "must not be null"), "planetId must not be null.");
        }

        if (isNull(model.getCoordinate())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "coordinate", "must not be null"), "coordinate must not be null.");
        }

        if (isNull(model.getSurfaceType())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "surfaceType", "must not be null"), "surfaceType must not be null.");
        }
    }
}
