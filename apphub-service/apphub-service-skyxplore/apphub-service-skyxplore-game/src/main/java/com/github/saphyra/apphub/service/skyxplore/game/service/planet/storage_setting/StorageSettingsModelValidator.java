package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingModel;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.PriorityValidator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
//TODO throw exception
class StorageSettingsModelValidator {
    private final ResourceDataService resourceDataService;
    private final PriorityValidator priorityValidator;

    void validate(StorageSettingModel request, Planet planet) {
        validate(request);

        if (planet.getStorageDetails().getStorageSettings().findByDataId(request.getDataId()).isPresent()) {
            throw new RuntimeException("StorageSetting for dataId " + request.getDataId() + " already exists."); //TODO proper exception
        }
    }

    void validate(StorageSettingModel request) {
        priorityValidator.validate(request.getPriority());

        if (isBlank(request.getDataId())) {

        }

        if (!resourceDataService.containsKey(request.getDataId())) {

        }

        if (isNull(request.getTargetAmount())) {

        }

        if (request.getTargetAmount() == 0) {

        }

        if (isNull(request.getBatchSize())) {

        }

        if (request.getBatchSize() < 1) {

        }
    }
}
