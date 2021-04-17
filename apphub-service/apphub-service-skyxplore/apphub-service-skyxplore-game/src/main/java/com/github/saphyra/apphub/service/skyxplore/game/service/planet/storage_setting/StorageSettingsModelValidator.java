package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.lib.exception.ConflictException;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.PriorityValidator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
class StorageSettingsModelValidator {
    private final ResourceDataService resourceDataService;
    private final PriorityValidator priorityValidator;

    void validate(StorageSettingModel model, Planet planet) {
        validate(model);

        if (planet.getStorageDetails().getStorageSettings().findByDataId(model.getDataId()).isPresent()) {
            throw new ConflictException(new ErrorMessage(ErrorCode.ALREADY_EXISTS.name()), "StorageSetting with dataId " + model.getDataId() + " already exists on planet " + planet.getPlanetId());
        }
    }

    void validate(StorageSettingModel model) {
        priorityValidator.validate(model.getPriority());

        if (isBlank(model.getDataId())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "dataId", "must not be blank"), "DataId is blank");
        }

        if (!resourceDataService.containsKey(model.getDataId())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "dataId", "unknown resource"), "DataId is unknown");
        }

        if (isNull(model.getTargetAmount())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "targetAmount", "must not be null"), "TargetAmount is null");
        }

        if (model.getTargetAmount() < 1) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "targetAmount", "too low"), "TargetAmount too low");
        }

        if (isNull(model.getBatchSize())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "batchSize", "must not be null"), "BatchSize is null");
        }

        if (model.getBatchSize() < 1) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "batchSize", "too low"), "BatchSize too low");
        }
    }
}
