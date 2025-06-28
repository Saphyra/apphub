package com.github.saphyra.apphub.service.skyxplore.game.service.planet.logistics;

import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request.ResourceDeliveryRequest;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request.ResourceDeliveryRequestFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_delivery.ResourceDeliveryProcessFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LogisticsServiceTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID RESOURCE_DELIVERY_REQUEST_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private ResourceDeliveryRequestFactory resourceDeliveryRequestFactory;

    @Mock
    private StoredResourceConverter storedResourceConverter;

    @Mock
    private ResourceDeliveryProcessFactory resourceDeliveryProcessFactory;

    @InjectMocks
    private LogisticsService underTest;

    @Mock
    private Game game;

    @Mock
    private StoredResource storedResource;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private GameData gameData;

    @Mock
    private ResourceDeliveryRequest resourceDeliveryRequest;

    @Mock
    private StoredResourceModel storedResourceModel;

    @Test
    void requestResourceDelivery() {
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(game.getData()).willReturn(gameData);
        given(resourceDeliveryRequestFactory.save(progressDiff, gameData, RESERVED_STORAGE_ID)).willReturn(resourceDeliveryRequest);
        given(resourceDeliveryRequest.getResourceDeliveryRequestId()).willReturn(RESOURCE_DELIVERY_REQUEST_ID);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(storedResourceConverter.toModel(GAME_ID, storedResource)).willReturn(storedResourceModel);

        underTest.requestResourceDelivery(game, PROCESS_ID, LOCATION, RESERVED_STORAGE_ID, List.of(storedResource));

        then(storedResource).should().setAllocatedBy(RESOURCE_DELIVERY_REQUEST_ID);
        then(progressDiff).should().save(storedResourceModel);
        then(resourceDeliveryProcessFactory).should().save(game, LOCATION, PROCESS_ID, RESOURCE_DELIVERY_REQUEST_ID);
    }
}