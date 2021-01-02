package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingsModel;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
//TODO throw exception
class StorageSettingsModelValidator {
    private final ResourceDataService resourceDataService;

    public void validate(StorageSettingsModel request) {
        if (isBlank(request.getDataId())) {

        }

        if (!resourceDataService.containsKey(request.getDataId())) {

        }

        if (isNull(request.getTargetAmount())) {

        }

        if (request.getTargetAmount() == 0) {

        }

        if (isNull(request.getPriority())) {

        }

        //TODO common priorityValidator
        if (request.getPriority() < 1) {

        }

        if (request.getPriority() > 10) {

        }

        if (isNull(request.getBatchSize())) {

        }

        if (request.getBatchSize() < 1) {

        }
    }
}
