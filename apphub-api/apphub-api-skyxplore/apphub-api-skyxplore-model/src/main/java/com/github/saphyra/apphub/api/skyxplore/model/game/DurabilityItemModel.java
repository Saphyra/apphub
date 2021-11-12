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
public class DurabilityItemModel extends GameItem {
    private UUID parent;
    private String metadata;
    private Integer maxDurability;
    private Integer currentDurability;
    private String dataId;
}
