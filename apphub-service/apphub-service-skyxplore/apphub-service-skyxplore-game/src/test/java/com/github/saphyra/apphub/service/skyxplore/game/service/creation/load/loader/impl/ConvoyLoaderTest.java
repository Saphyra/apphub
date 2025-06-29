package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConvoyModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy.Convoy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy.Convoys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ConvoyLoaderTest {
    private static final UUID CONVOY_ID = UUID.randomUUID();
    private static final UUID RESOURCE_DELIVERY_REQUEST_ID = UUID.randomUUID();
    private static final Integer CAPACITY = 24;

    @InjectMocks
    private ConvoyLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Convoys convoys;

    @Mock
    private Convoy convoy;

    @Mock
    private ConvoyModel convoyModel;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.CONVOY);
    }

    @Test
    void addToGameData() {
        given(gameData.getConvoys()).willReturn(convoys);

        underTest.addToGameData(gameData, List.of(convoy));

        then(convoys).should().addAll(List.of(convoy));
    }

    @Test
    void convert() {
        given(convoyModel.getId()).willReturn(CONVOY_ID);
        given(convoyModel.getResourceDeliveryRequestId()).willReturn(RESOURCE_DELIVERY_REQUEST_ID);
        given(convoyModel.getCapacity()).willReturn(CAPACITY);

        assertThat(underTest.convert(convoyModel))
            .returns(CONVOY_ID, Convoy::getConvoyId)
            .returns(RESOURCE_DELIVERY_REQUEST_ID, Convoy::getResourceDeliveryRequestId)
            .returns(CAPACITY, Convoy::getCapacity);
    }
}