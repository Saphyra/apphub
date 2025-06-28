package com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConvoyModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ConvoyConverterTest {
    private static final UUID CONVOY_ID = UUID.randomUUID();
    private static final UUID RESOURCE_DELIVERY_REQUEST_ID = UUID.randomUUID();
    private static final int CAPACITY = 23;
    private static final UUID GAME_ID = UUID.randomUUID();

    @InjectMocks
    private ConvoyConverter underTest;

    @Test
    void toModel() {
        Convoy convoy = Convoy.builder()
            .convoyId(CONVOY_ID)
            .resourceDeliveryRequestId(RESOURCE_DELIVERY_REQUEST_ID)
            .capacity(CAPACITY)
            .build();

        assertThat(underTest.toModel(GAME_ID, convoy))
            .returns(CONVOY_ID, GameItem::getId)
            .returns(GameItemType.CONVOY, GameItem::getType)
            .returns(RESOURCE_DELIVERY_REQUEST_ID, ConvoyModel::getResourceDeliveryRequestId)
            .returns(CAPACITY, ConvoyModel::getCapacity);
    }
}