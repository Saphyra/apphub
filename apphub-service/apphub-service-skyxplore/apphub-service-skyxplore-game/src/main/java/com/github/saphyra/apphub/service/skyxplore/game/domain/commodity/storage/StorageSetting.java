package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class StorageSetting {
    private final UUID storageSettingsId;
    private final String dataId;
    private int targetAmount;
    private int priority;
    private int batchSize;
}
