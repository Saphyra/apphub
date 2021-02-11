package com.github.saphyra.apphub.api.skyxplore.model.game;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class StoredResourceModel extends GameItem {
    private UUID location;
    private String locationType;
    private String storageType;
    private String dataId;
    private int amount;
}
