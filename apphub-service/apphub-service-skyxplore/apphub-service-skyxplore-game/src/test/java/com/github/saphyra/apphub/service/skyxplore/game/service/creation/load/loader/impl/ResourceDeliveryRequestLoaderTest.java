package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ResourceDeliveryRequestModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request.ResourceDeliveryRequest;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request.ResourceDeliveryRequests;
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
class ResourceDeliveryRequestLoaderTest {
    private static final UUID RESOURCE_DELIVERY_REQUEST_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();

    @InjectMocks
    private ResourceDeliveryRequestLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private ResourceDeliveryRequest resourceDeliveryRequest;

    @Mock
    private ResourceDeliveryRequests resourceDeliveryRequests;

    @Mock
    private ResourceDeliveryRequestModel model;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.RESOURCE_DELIVERY_REQUEST);
    }

    @Test
    void addToGameData() {
        given(gameData.getResourceDeliveryRequests()).willReturn(resourceDeliveryRequests);

        underTest.addToGameData(gameData, List.of(resourceDeliveryRequest));

        then(resourceDeliveryRequests).should().addAll(List.of(resourceDeliveryRequest));
    }

    @Test
    void convert() {
        given(model.getId()).willReturn(RESOURCE_DELIVERY_REQUEST_ID);
        given(model.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);

        assertThat(underTest.convert(model))
            .returns(RESOURCE_DELIVERY_REQUEST_ID, ResourceDeliveryRequest::getResourceDeliveryRequestId)
            .returns(RESERVED_STORAGE_ID, ResourceDeliveryRequest::getReservedStorageId);
    }
}