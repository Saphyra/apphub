package com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ResourceDeliveryRequestModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResourceDeliveryRequestConverter {
    public ResourceDeliveryRequestModel toModel(UUID gameId, ResourceDeliveryRequest deliveryRequest) {
        ResourceDeliveryRequestModel model = new ResourceDeliveryRequestModel();
        model.setId(deliveryRequest.getResourceDeliveryRequestId());
        model.setType(GameItemType.RESOURCE_DELIVERY_REQUEST);
        model.setGameId(gameId);
        model.setReservedStorageId(deliveryRequest.getReservedStorageId());

        return model;
    }
}
