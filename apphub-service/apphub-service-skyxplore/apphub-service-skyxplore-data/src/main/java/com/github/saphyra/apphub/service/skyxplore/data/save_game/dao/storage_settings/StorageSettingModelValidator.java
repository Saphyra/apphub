package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.storage_settings;

import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
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
            throw ExceptionFactory.invalidParam("location", "must not be null");
        }

        if (isNull(model.getLocationType())) {
            throw ExceptionFactory.invalidParam("locationType", "must not be null");
        }

        if (isNull(model.getDataId())) {
            throw ExceptionFactory.invalidParam("dataId", "must not be null");
        }

        if (isNull(model.getTargetAmount())) {
            throw ExceptionFactory.invalidParam("targetAmount", "must not be null");
        }

        if (isNull(model.getPriority())) {
            throw ExceptionFactory.invalidParam("priority", "must not be null");
        }

        if (isNull(model.getBatchSize())) {
            throw ExceptionFactory.invalidParam("batchSize", "must not be null");
        }
    }
}
