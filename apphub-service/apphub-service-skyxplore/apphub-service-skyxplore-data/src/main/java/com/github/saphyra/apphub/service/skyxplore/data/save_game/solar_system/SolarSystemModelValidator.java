package com.github.saphyra.apphub.service.skyxplore.data.save_game.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
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
public class SolarSystemModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(SolarSystemModel model) {
        gameItemValidator.validate(model);

        if (isNull(model.getRadius())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "radius", "must not be null"), "radius must not be null.");
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
    }
}
