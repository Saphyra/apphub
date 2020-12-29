package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class StoredResource {
    private final UUID storedResourceId;
    private final StorageType storageType;
    private final String dataId;
    private int amount;
}
