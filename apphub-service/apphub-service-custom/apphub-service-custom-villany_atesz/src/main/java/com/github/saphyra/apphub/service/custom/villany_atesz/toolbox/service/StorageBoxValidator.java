package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StorageBoxModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box.StorageBoxDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class StorageBoxValidator {
    private final StorageBoxDao storageBoxDao;

    void validate(StorageBoxModel storageBox) {
        if (isNull(storageBox.getStorageBoxId())) {
            ValidationUtil.notNull(storageBox.getName(), "storageBox.name");
        } else if (!storageBoxDao.exists(storageBox.getStorageBoxId())) {
            throw ExceptionFactory.invalidParam("storageBoxId", "not found");
        }
    }
}
