package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.PriorityValidator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
class StorageSettingsModelValidator {
    private final ResourceDataService resourceDataService;
    private final PriorityValidator priorityValidator;

    void validate(GameData gameData, UUID location, StorageSettingApiModel model) {
        validate(model);

        boolean storageSettingAlreadyExists = gameData.getStorageSettings()
            .findByLocationAndDataId(location, model.getDataId())
            .isPresent();
        if (storageSettingAlreadyExists) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, String.format("StorageSetting with dataId %s alreaddy exists on planet %s", model.getDataId(), location));
        }
    }

    void validate(StorageSettingApiModel model) {
        priorityValidator.validate(model.getPriority());

        if (isBlank(model.getDataId())) {
            throw ExceptionFactory.invalidParam("dataId", "must not be null or blank");
        }

        if (!resourceDataService.containsKey(model.getDataId())) {
            throw ExceptionFactory.invalidParam("dataId", "unknown resource");
        }

        if (isNull(model.getTargetAmount())) {
            throw ExceptionFactory.invalidParam("targetAmount", "must not be null");
        }

        if (model.getTargetAmount() < 0) {
            throw ExceptionFactory.invalidParam("targetAmount", "too low");
        }

        if (isNull(model.getBatchSize())) {
            throw ExceptionFactory.invalidParam("batchSize", "must not be null");
        }

        if (model.getBatchSize() < 1) {
            throw ExceptionFactory.invalidParam("batchSize", "too low");
        }
    }
}
