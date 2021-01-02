package com.github.saphyra.apphub.api.skyxplore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StorageSettingsModel {
    private UUID storageSettingId;
    private String dataId;
    private Integer targetAmount;
    private Integer priority;
    private Integer batchSize;
}
