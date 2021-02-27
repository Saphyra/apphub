package com.github.saphyra.apphub.service.skyxplore.data.save_game.planet;

import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanetModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(PlanetModel model) {
        gameItemValidator.validate(model);

        if (isNull(model.getSolarSystemId())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "solarSystemId", "must not be null"), "solarSystemId must not be null.");
        }

        if (isNull(model.getDefaultName())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "defaultName", "must not be null"), "defaultName must not be null.");
        }

        if (isNull(model.getCustomNames())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "customNames", "must not be null"), "customNames must not be null.");
        }

        if (model.getCustomNames().values().stream().anyMatch(Objects::isNull)) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "customNames", "must not contain null"), "customNames must not contain null.");
        }

        if (isNull(model.getCoordinate())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "coordinate", "must not be null"), "coordinate must not be null.");
        }

        if (isNull(model.getSize())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "size", "must not be null"), "size must not be null.");
        }
    }
}
