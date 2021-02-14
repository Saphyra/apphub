package com.github.saphyra.apphub.service.skyxplore.data.save_game.priority;

import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
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
public class PriorityModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(PriorityModel model) {
        gameItemValidator.validateWithoutId(model);

        if (isNull(model.getLocation())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "location", "must not be null."), "location must not be null.");
        }
        if (isNull(model.getLocationType())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "locationType", "must not be null."), "locationType must not be null.");
        }

        if (isNull(model.getPriorityType())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "priorityType", "must not be null."), "priorityType must not be null.");
        }

        if (isNull(model.getValue())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "value", "must not be null."), "value must not be null.");
        }
    }
}
