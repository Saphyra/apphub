package com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting;

import java.util.UUID;
import java.util.Vector;

//TODO unit test
public class StorageSettings extends Vector<StorageSetting> {
    public void deleteByStorageSettingId(UUID storageSettingId) {
        removeIf(storageSetting -> storageSetting.getStorageSettingId().equals(storageSettingId));
    }
}
