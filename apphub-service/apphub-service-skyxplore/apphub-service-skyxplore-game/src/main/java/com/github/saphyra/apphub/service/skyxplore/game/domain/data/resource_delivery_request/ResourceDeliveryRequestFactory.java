package com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResourceDeliveryRequestFactory {
    private final IdGenerator idGenerator;
    private final ResourceDeliveryRequestConverter resourceDeliveryRequestConverter;

    public ResourceDeliveryRequest create(UUID reservedStorageId) {
        return ResourceDeliveryRequest.builder()
            .resourceDeliveryRequestId(idGenerator.randomUuid())
            .reservedStorageId(reservedStorageId)
            .build();
    }

    public ResourceDeliveryRequest save(GameProgressDiff progressDiff, GameData gameData, UUID reservedStorageId) {
        ResourceDeliveryRequest deliveryRequest = create(reservedStorageId);

        gameData.getResourceDeliveryRequests()
            .add(deliveryRequest);

        progressDiff.save(resourceDeliveryRequestConverter.toModel(gameData.getGameId(), deliveryRequest));

        return deliveryRequest;
    }
}
