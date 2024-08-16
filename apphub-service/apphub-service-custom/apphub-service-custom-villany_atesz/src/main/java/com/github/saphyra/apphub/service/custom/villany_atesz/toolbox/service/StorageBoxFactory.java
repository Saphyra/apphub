package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StorageBoxModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box.StorageBox;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box.StorageBoxDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static io.micrometer.common.util.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
class StorageBoxFactory {
    private final IdGenerator idGenerator;
    private final StorageBoxDao storageBoxDao;

    UUID getStorageBoxId(UUID userId, StorageBoxModel storageBox) {
        return Optional.ofNullable(storageBox.getStorageBoxId())
            .orElseGet(() -> createStorageBox(userId, storageBox.getName()));
    }

    private UUID createStorageBox(UUID userId, String name) {
        if (isBlank(name)) {
            return null;
        }

        log.info("Creating new storageBox for userId {}", userId);

        StorageBox storageBox = StorageBox.builder()
            .storageBoxId(idGenerator.randomUuid())
            .userId(userId)
            .name(name)
            .build();

        storageBoxDao.save(storageBox);

        return storageBox.getStorageBoxId();
    }
}
