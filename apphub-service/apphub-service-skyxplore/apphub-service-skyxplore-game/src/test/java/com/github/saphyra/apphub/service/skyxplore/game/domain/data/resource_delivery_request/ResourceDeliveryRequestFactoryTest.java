package com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request;

import com.github.saphyra.apphub.api.skyxplore.model.game.ResourceDeliveryRequestModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ResourceDeliveryRequestFactoryTest {
    private static final UUID RESOURCE_DELIVERY_REQUEST_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ResourceDeliveryRequestConverter resourceDeliveryRequestConverter;

    @InjectMocks
    private ResourceDeliveryRequestFactory underTest;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private GameData gameData;

    @Mock
    private ResourceDeliveryRequestModel model;

    @Mock
    private ResourceDeliveryRequests resourceDeliveryRequests;

    @Test
    void save() {
        given(idGenerator.randomUuid()).willReturn(RESOURCE_DELIVERY_REQUEST_ID);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(gameData.getResourceDeliveryRequests()).willReturn(resourceDeliveryRequests);
        given(resourceDeliveryRequestConverter.toModel(eq(GAME_ID), any())).willReturn(model);

        ResourceDeliveryRequest result = underTest.save(progressDiff, gameData, RESERVED_STORAGE_ID);

        assertThat(result)
            .returns(RESOURCE_DELIVERY_REQUEST_ID, ResourceDeliveryRequest::getResourceDeliveryRequestId)
            .returns(RESERVED_STORAGE_ID, ResourceDeliveryRequest::getReservedStorageId);

        then(resourceDeliveryRequests).should().add(result);
        then(progressDiff).should().save(model);
    }
}