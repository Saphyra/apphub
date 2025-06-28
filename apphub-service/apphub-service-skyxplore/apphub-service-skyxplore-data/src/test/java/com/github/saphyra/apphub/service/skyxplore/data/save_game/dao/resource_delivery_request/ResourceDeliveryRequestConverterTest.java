package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.resource_delivery_request;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ResourceDeliveryRequestModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ResourceDeliveryRequestConverterTest {
    private static final UUID RESOURCE_DELIVERY_REQUEST_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final String RESOURCE_DELIVERY_REQUEST_ID_STRING = "resource-delivery-request-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String RESERVED_STORAGE_ID_STRING = "reserved-storage-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ResourceDeliveryRequestConverter underTest;

    @Test
    void convertDomain() {
        ResourceDeliveryRequestModel model = new ResourceDeliveryRequestModel();
        model.setId(RESOURCE_DELIVERY_REQUEST_ID);
        model.setGameId(GAME_ID);
        model.setReservedStorageId(RESERVED_STORAGE_ID);

        given(uuidConverter.convertDomain(RESOURCE_DELIVERY_REQUEST_ID)).willReturn(RESOURCE_DELIVERY_REQUEST_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(RESERVED_STORAGE_ID)).willReturn(RESERVED_STORAGE_ID_STRING);

        assertThat(underTest.convertDomain(model))
            .returns(RESOURCE_DELIVERY_REQUEST_ID_STRING, ResourceDeliveryRequestEntity::getResourceDeliveryRequestId)
            .returns(GAME_ID_STRING, ResourceDeliveryRequestEntity::getGameId)
            .returns(RESERVED_STORAGE_ID_STRING, ResourceDeliveryRequestEntity::getReservedStorageId);
    }

    @Test
    void convertEntity() {
        ResourceDeliveryRequestEntity entity = ResourceDeliveryRequestEntity.builder()
            .resourceDeliveryRequestId(RESOURCE_DELIVERY_REQUEST_ID_STRING)
            .gameId(GAME_ID_STRING)
            .reservedStorageId(RESERVED_STORAGE_ID_STRING)
            .build();

        given(uuidConverter.convertEntity(RESOURCE_DELIVERY_REQUEST_ID_STRING)).willReturn(RESOURCE_DELIVERY_REQUEST_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(RESERVED_STORAGE_ID_STRING)).willReturn(RESERVED_STORAGE_ID);

        assertThat(underTest.convertEntity(entity))
            .returns(RESOURCE_DELIVERY_REQUEST_ID, ResourceDeliveryRequestModel::getId)
            .returns(GameItemType.RESOURCE_DELIVERY_REQUEST, GameItem::getType)
            .returns(GAME_ID, ResourceDeliveryRequestModel::getGameId)
            .returns(RESERVED_STORAGE_ID, ResourceDeliveryRequestModel::getReservedStorageId);
    }
}