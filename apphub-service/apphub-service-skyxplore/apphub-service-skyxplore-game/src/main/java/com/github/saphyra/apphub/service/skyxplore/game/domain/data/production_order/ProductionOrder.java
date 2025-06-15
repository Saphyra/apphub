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

    //TODO unit test
    public boolean allStarted() {
        return startedAmount >= requestedAmount;
    }

    //TODO unit test
    public int getMissingAmount() {
        return requestedAmount - startedAmount;
    }

    //TODO unit test
    public void increaseStartedAmount(int amountToStart) {
        this.startedAmount += amountToStart;
    }
}
