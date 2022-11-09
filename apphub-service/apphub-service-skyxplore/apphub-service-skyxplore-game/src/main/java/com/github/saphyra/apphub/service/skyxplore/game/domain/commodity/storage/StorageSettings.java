package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

@NoArgsConstructor
public class StorageSettings extends Vector<StorageSetting> {
    public StorageSettings(Collection<StorageSetting> storageSettings) {
        super(storageSettings);
    }

    public Optional<StorageSetting> findByDataId(String dataId) {
        return stream()
            .filter(storageSetting -> storageSetting.getDataId().equals(dataId))
            .findAny();
    }

    public void deleteByStorageSettingId(UUID storageSettingId) {
        removeIf(storageSetting -> storageSetting.getStorageSettingId().equals(storageSettingId));
    }

    public Optional<StorageSetting> findByStorageSettingId(UUID storageSettingId) {
        return stream()
            .filter(storageSetting -> storageSetting.getStorageSettingId().equals(storageSettingId))
            .findFirst();
    }

    public StorageSetting findByStorageSettingIdValidated(UUID storageSettingId) {
        return findByStorageSettingId(storageSettingId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "StorageSetting not found with storageSettingId " + storageSettingId));
    }
}
