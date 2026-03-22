package com.github.saphyra.apphub.service.feature.skyxplore.game.domain.data.resource_delivery_request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ResourceDeliveryRequest {
    private final UUID resourceDeliveryRequestId;
    private final UUID reservedStorageId;
    @Builder.Default
    private boolean existing = false;
}
