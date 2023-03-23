package com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting;

import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

//TODO unit test
public class StorageSettings extends Vector<StorageSetting> {
    public void deleteByStorageSettingId(UUID storageSettingId) {
        removeIf(storageSetting -> storageSetting.getStorageSettingId().equals(storageSettingId));
    }

    public StorageSetting findByStorageSettingIdValidated(UUID storageSettingId) {
        return stream()
            .filter(storageSetting -> storageSetting.getStorageSettingId().equals(storageSettingId))
            .findAny()
            .orElseThrow();
    }

    public List<StorageSetting> getByLocation(UUID location) {
        return stream()
            .filter(storageSetting -> storageSetting.getLocation().equals(location))
            .collect(Collectors.toList());
    }
}
