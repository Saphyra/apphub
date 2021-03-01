package com.github.saphyra.apphub.service.skyxplore.data.save_game.storage_settings;

import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
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
public class StorageSettingModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(StorageSettingModel model) {
        gameItemValidator.validate(model);

        if (isNull(model.getLocation())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "location", "must not be null"), "location must not be null.");
        }

        if (isNull(model.getLocationType())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "locationType", "must not be null"), "locationType must not be null.");
        }

        if (isNull(model.getDataId())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "dataId", "must not be null"), "dataId must not be null.");
        }

        if (isNull(model.getTargetAmount())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "targetAmount", "must not be null"), "targetAmount must not be null.");
        }

        if (isNull(model.getPriority())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "priority", "must not be null"), "priority must not be null.");
        }

        if (isNull(model.getBatchSize())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "batchSize", "must not be null"), "batchSize must not be null.");
        }
    }
}
