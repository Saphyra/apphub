package com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ResourceDeliveryRequestModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ResourceDeliveryRequestConverterTest {
    private static final UUID RESOURCE_DELIVERY_REQUEST_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @InjectMocks
    private ResourceDeliveryRequestConverter underTest;

    @Test
    void toModel() {
        ResourceDeliveryRequest request = ResourceDeliveryRequest.builder()
            .resourceDeliveryRequestId(RESOURCE_DELIVERY_REQUEST_ID)
            .reservedStorageId(RESERVED_STORAGE_ID)
            .build();

        assertThat(underTest.toModel(GAME_ID, request))
            .returns(RESOURCE_DELIVERY_REQUEST_ID, GameItem::getId)
            .returns(GameItemType.RESOURCE_DELIVERY_REQUEST, GameItem::getType)
            .returns(GAME_ID, GameItem::getGameId)
            .returns(RESERVED_STORAGE_ID, ResourceDeliveryRequestModel::getReservedStorageId);
    }
}