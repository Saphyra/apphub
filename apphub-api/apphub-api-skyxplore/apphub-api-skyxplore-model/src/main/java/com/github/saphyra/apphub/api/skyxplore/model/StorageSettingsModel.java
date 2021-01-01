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
    private UUID storageSettingsId;
    private String dataId;
    private int targetAmount;
    private int priority;
    private int batchSize;
}
