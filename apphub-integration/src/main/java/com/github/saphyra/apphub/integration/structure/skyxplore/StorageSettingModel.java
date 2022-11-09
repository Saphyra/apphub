package com.github.saphyra.apphub.integration.structure.skyxplore;

import com.github.saphyra.apphub.integration.framework.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class StorageSettingModel {
    private static final Integer TARGET_AMOUNT = 300;
    private static final Integer PRIORITY = 4;
    private static final Integer BATCH_SIZE = 13;

    private UUID storageSettingId;
    private String dataId;
    private Integer targetAmount;
    private Integer priority;
    private Integer batchSize;

    public static StorageSettingModel valid() {
        return StorageSettingModel.builder()
            .dataId(Constants.DATA_ID_ORE)
            .targetAmount(TARGET_AMOUNT)
            .priority(PRIORITY)
            .batchSize(BATCH_SIZE)
            .build();
    }

    public static StorageSettingModel valid(UUID storageSettingId) {
        return valid()
            .toBuilder()
            .storageSettingId(storageSettingId)
            .build();
    }
}
