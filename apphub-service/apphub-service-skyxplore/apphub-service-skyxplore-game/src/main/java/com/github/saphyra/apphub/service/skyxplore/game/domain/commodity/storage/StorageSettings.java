package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import java.util.Optional;
import java.util.Vector;

public class StorageSettings extends Vector<StorageSetting> {
    public Optional<StorageSetting> findByDataId(String dataId) {
        return stream()
            .filter(storageSetting -> storageSetting.getDataId().equals(dataId))
            .findAny();
    }
}
