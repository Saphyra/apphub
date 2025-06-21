package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.convoy;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConvoyModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
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
class ConvoyConverterTest {
    private static final UUID CONVOY_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID RESOURCE_DELIVERY_REQUEST_ID = UUID.randomUUID();
    private static final Integer CAPACITY = 23;
    private static final String CONVOY_ID_STRING = "convoy-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String RESOURCE_DELIVERY_REQUEST_ID_STRING = "resource-delivery-request-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ConvoyConverter underTest;

    @Test
    void convertDomain() {
        ConvoyModel model = new ConvoyModel();
        model.setId(CONVOY_ID);
        model.setGameId(GAME_ID);
        model.setResourceDeliveryRequestId(RESOURCE_DELIVERY_REQUEST_ID);
        model.setCapacity(CAPACITY);

        given(uuidConverter.convertDomain(CONVOY_ID)).willReturn(CONVOY_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(RESOURCE_DELIVERY_REQUEST_ID)).willReturn(RESOURCE_DELIVERY_REQUEST_ID_STRING);

        assertThat(underTest.convertDomain(model))
            .returns(CONVOY_ID_STRING, ConvoyEntity::getConvoyId)
            .returns(GAME_ID_STRING, ConvoyEntity::getGameId)
            .returns(RESOURCE_DELIVERY_REQUEST_ID_STRING, ConvoyEntity::getResourceDeliveryRequestId)
            .returns(CAPACITY, ConvoyEntity::getCapacity);
    }

    @Test
    void convertEntity() {
        ConvoyEntity entity = ConvoyEntity.builder()
            .convoyId(CONVOY_ID_STRING)
            .gameId(GAME_ID_STRING)
            .resourceDeliveryRequestId(RESOURCE_DELIVERY_REQUEST_ID_STRING)
            .capacity(CAPACITY)
            .build();

        given(uuidConverter.convertEntity(CONVOY_ID_STRING)).willReturn(CONVOY_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(RESOURCE_DELIVERY_REQUEST_ID_STRING)).willReturn(RESOURCE_DELIVERY_REQUEST_ID);

        assertThat(underTest.convertEntity(entity))
            .returns(CONVOY_ID, ConvoyModel::getId)
            .returns(GameItemType.CONVOY, ConvoyModel::getType)
            .returns(GAME_ID, ConvoyModel::getGameId)
            .returns(RESOURCE_DELIVERY_REQUEST_ID, ConvoyModel::getResourceDeliveryRequestId)
            .returns(CAPACITY, ConvoyModel::getCapacity);
    }
}