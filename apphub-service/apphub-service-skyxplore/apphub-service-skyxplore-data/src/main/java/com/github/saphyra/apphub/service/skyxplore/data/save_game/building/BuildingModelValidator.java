package com.github.saphyra.apphub.service.skyxplore.data.save_game.building;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
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
public class BuildingModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(BuildingModel model) {
        gameItemValidator.validate(model);

        if (isNull(model.getSurfaceId())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "surfaceId", "must not be null"), "surfaceId must not be null.");
        }

        if (isNull(model.getDataId())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "dataId", "must not be null"), "dataId must not be null.");
        }

        if (isNull(model.getLevel())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "level", "must not be null"), "level must not be null.");
        }
    }
}
