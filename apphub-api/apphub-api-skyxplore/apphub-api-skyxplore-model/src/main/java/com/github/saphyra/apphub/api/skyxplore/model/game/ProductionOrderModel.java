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
    private UUID productionRequestId;
    private UUID constructionAreaId;
    private String resourceDataId;
    private Integer requestedAmount;
    private Integer startedAmount;
}
