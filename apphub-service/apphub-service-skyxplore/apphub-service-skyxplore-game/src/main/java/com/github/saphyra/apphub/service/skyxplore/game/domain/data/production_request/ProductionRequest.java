package com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class ProductionRequest {
    private final UUID productionRequestId;
    private final UUID reservedStorageId; //Target where the resource has to be delivered once it is produced
    private final int requestedAmount;
    private int dispatchedAmount;

    public void increaseDispatchedAmount(int dispatched) {
        dispatchedAmount += dispatched;
    }
}
