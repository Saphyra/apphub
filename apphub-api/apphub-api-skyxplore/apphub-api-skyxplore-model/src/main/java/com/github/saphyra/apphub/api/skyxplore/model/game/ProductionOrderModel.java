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
public class ProductionOrderModel extends GameItem {
    private UUID location;
    private String locationType;
    private UUID assignee;
    private UUID externalReference;
    private String dataId;
    private Integer amount;
    private Integer requiredWorkPoints;
    private Integer currentWorkPoints;
}
