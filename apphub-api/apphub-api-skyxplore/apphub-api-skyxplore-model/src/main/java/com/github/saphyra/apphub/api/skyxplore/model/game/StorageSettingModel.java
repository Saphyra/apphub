package com.github.saphyra.apphub.api.skyxplore.model.game;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StorageSettingModel extends GameItem {
    private UUID location;
    private String locationType;
    private String dataId;
    private int targetAmount;
    private int priority;
    private int batchSize;
}
