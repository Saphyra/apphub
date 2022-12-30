package com.github.saphyra.apphub.service.platform.encryption.shared_data.service;

import com.github.saphyra.apphub.api.platform.encryption.model.SharedData;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class SharedDataValidator {
    void validateForCreation(SharedData sharedData) {
        ValidationUtil.notNull(sharedData.getExternalId(), "externalId");
        ValidationUtil.notNull(sharedData.getDataType(), "dataType");
        ValidationUtil.notAllNull(List.of("sharedWith", "publicData"), sharedData.getSharedWith(), sharedData.getPublicData());
        ValidationUtil.notNull(sharedData.getAccessMode(), "accessMode");
    }

    public void validateForCloning(SharedData sharedData) {
        ValidationUtil.notNull(sharedData.getExternalId(), "externalId");
        ValidationUtil.notNull(sharedData.getDataType(), "dataType");
    }
}
