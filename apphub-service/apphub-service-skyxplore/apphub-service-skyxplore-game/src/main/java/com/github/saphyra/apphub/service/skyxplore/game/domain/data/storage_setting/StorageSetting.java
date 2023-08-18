package com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class StorageSetting {
    private final UUID storageSettingId;
    private final UUID location;
    private final String dataId;
    private Integer targetAmount;
    private Integer priority;
}
