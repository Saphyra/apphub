package com.github.saphyra.apphub.service.platform.encryption.shared_data.service;

import com.github.saphyra.apphub.api.platform.encryption.model.SharedData;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao.EncryptionKeyDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class SharedDataValidator {
    private final EncryptionKeyDao encryptionKeyDao;

    void validateForCreation(SharedData sharedData) {
        ValidationUtil.notNull(sharedData.getExternalId(), "externalId");
        ValidationUtil.notNull(sharedData.getDataType(), "dataType");
        ValidationUtil.notAllNull(List.of("sharedWith", "publicData"), sharedData.getSharedWith(), sharedData.getPublicData());
        ValidationUtil.notNull(sharedData.getAccessMode(), "accessMode");

        if (encryptionKeyDao.findById(sharedData.getExternalId(), sharedData.getDataType()).isEmpty()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "EncryptionKey not found for " + sharedData);
        }
    }

    public void validateForCloning(SharedData sharedData) {
        ValidationUtil.notNull(sharedData.getExternalId(), "externalId");
        ValidationUtil.notNull(sharedData.getDataType(), "dataType");
    }
}
