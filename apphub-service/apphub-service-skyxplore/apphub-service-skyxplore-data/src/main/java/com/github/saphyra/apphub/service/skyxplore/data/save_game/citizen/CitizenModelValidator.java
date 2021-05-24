package com.github.saphyra.apphub.service.skyxplore.data.save_game.citizen;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
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
public class CitizenModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(CitizenModel citizenModel) {
        gameItemValidator.validate(citizenModel);

        if (isNull(citizenModel.getLocation())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "location", "must not be null"), "location must not be null.");
        }

        if (isNull(citizenModel.getLocationType())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "locationType", "must not be null"), "locationType must not be null.");
        }

        if (isNull(citizenModel.getName())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "name", "must not be null"), "name must not be null.");
        }

        if (isNull(citizenModel.getMorale())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "morale", "must not be null"), "morale must not be null.");
        }

        if (isNull(citizenModel.getSatiety())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "satiety", "must not be null"), "satiety must not be null.");
        }
    }
}
