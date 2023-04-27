package com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

public class StorageSettings extends Vector<StorageSetting> {
    public void deleteByStorageSettingId(UUID storageSettingId) {
        removeIf(storageSetting -> storageSetting.getStorageSettingId().equals(storageSettingId));
    }

    public StorageSetting findByStorageSettingIdValidated(UUID storageSettingId) {
        return stream()
            .filter(storageSetting -> storageSetting.getStorageSettingId().equals(storageSettingId))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "StorageSetting not found by storageSettingId " + storageSettingId));
    }

    public List<StorageSetting> getByLocation(UUID location) {
        return stream()
            .filter(storageSetting -> storageSetting.getLocation().equals(location))
            .collect(Collectors.toList());
    }

    public Optional<StorageSetting> findByLocationAndDataId(UUID location, String dataId) {
        return stream()
            .filter(storageSetting -> storageSetting.getLocation().equals(location))
            .filter(storageSetting -> storageSetting.getDataId().equals(dataId))
            .findAny();
    }
}
