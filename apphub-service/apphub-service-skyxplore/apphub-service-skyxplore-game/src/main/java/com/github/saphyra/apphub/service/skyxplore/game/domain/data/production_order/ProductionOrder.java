package com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Slf4j
public class ProductionOrder {
    private final UUID productionOrderId;
    private final UUID productionRequestId;
    private final UUID constructionAreaId;
    private final String resourceDataId;
    private final int requestedAmount;
    private int startedAmount;

    public boolean allStarted() {
        return startedAmount >= requestedAmount;
    }

    public int getMissingAmount() {
        return requestedAmount - startedAmount;
    }

    public void increaseStartedAmount(int amountToStart) {
        this.startedAmount += amountToStart;
    }
}
