package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_request;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy.Convoy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy.Convoys;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request.ProductionRequest;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request.ProductionRequestFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request.ProductionRequests;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request.ResourceDeliveryRequest;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request.ResourceDeliveryRequests;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.logistics.LogisticsService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StoredResourceService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_dispatcher.ProductionDispatcherProcessFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ResourceRequestProcessHelperTest {
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final Integer TOTAL_AMOUNT = 1000;
    private static final UUID RESOURCE_DELIVERY_REQUEST_ID = UUID.randomUUID();
    private static final UUID CONVOY_ID = UUID.randomUUID();
    private static final Integer DELIVERED_AMOUNT = 300;
    private static final Integer REQUESTED_AMOUNT = 200;
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final int MISSING_AMOUNT = 234;
    private static final UUID PRODUCTION_REQUEST_ID = UUID.randomUUID();

    @Mock
    private StoredResourceService storedResourceService;

    @Mock
    private LogisticsService logisticsService;

    @Mock
    private ProductionRequestFactory productionRequestFactory;

    @Mock
    private ProductionDispatcherProcessFactory productionDispatcherProcessFactory;

    @InjectMocks
    private ResourceRequestProcessHelper underTest;

    @Mock
    private GameData gameData;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private ResourceDeliveryRequests resourceDeliveryRequests;

    @Mock
    private ResourceDeliveryRequest resourceDeliveryRequest;

    @Mock
    private Convoys convoys;

    @Mock
    private Convoy convoy;

    @Mock
    private StoredResources storedResources;

    @Mock
    private StoredResource storedResource;

    @Mock
    private ProductionRequests productionRequests;

    @Mock
    private ProductionRequest productionRequest;

    @Mock
    private Game game;

    @Mock
    private GameProgressDiff progressDiff;

    @Test
    void getMissingResources() {
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.findByIdValidated(RESERVED_STORAGE_ID)).willReturn(reservedStorage);
        given(reservedStorage.getAmount()).willReturn(TOTAL_AMOUNT);
        given(gameData.getResourceDeliveryRequests()).willReturn(resourceDeliveryRequests);
        given(resourceDeliveryRequests.getByReservedStorageId(RESERVED_STORAGE_ID)).willReturn(List.of(resourceDeliveryRequest));
        given(gameData.getConvoys()).willReturn(convoys);
        given(resourceDeliveryRequest.getResourceDeliveryRequestId()).willReturn(RESOURCE_DELIVERY_REQUEST_ID);
        given(convoys.getByResourceDeliveryRequestId(RESOURCE_DELIVERY_REQUEST_ID)).willReturn(List.of(convoy));
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(convoy.getConvoyId()).willReturn(CONVOY_ID);
        given(storedResources.findByContainerId(convoy.getConvoyId())).willReturn(Optional.of(storedResource));
        given(storedResource.getAmount()).willReturn(DELIVERED_AMOUNT);
        given(gameData.getProductionRequests()).willReturn(productionRequests);
        given(productionRequests.getByReservedStorageId(RESERVED_STORAGE_ID)).willReturn(List.of(productionRequest));
        given(productionRequest.getRequestedAmount()).willReturn(REQUESTED_AMOUNT);

        assertThat(underTest.getMissingResources(gameData, RESERVED_STORAGE_ID))
            .returns(TOTAL_AMOUNT - DELIVERED_AMOUNT, MissingResources::getToDeliver)
            .returns(TOTAL_AMOUNT - DELIVERED_AMOUNT - REQUESTED_AMOUNT, MissingResources::getToRequest);
    }

    @Test
    void initiateDelivery() {
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(game.getData()).willReturn(gameData);
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.findByIdValidated(RESERVED_STORAGE_ID)).willReturn(reservedStorage);
        given(reservedStorage.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(storedResourceService.prepareBatch(progressDiff, gameData, LOCATION, RESOURCE_DATA_ID, MISSING_AMOUNT)).willReturn(List.of(storedResource));
        given(storedResource.getAmount()).willReturn(DELIVERED_AMOUNT);

        assertThat(underTest.initiateDelivery(game, PROCESS_ID, LOCATION, RESERVED_STORAGE_ID, MISSING_AMOUNT)).isEqualTo(DELIVERED_AMOUNT);

        then(logisticsService).should().requestResourceDelivery(game, PROCESS_ID, LOCATION, RESERVED_STORAGE_ID, List.of(storedResource));
    }

    @Test
    void createProductionRequest() {
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(game.getData()).willReturn(gameData);
        given(productionRequestFactory.save(progressDiff, gameData, RESERVED_STORAGE_ID, REQUESTED_AMOUNT)).willReturn(productionRequest);
        given(productionRequest.getProductionRequestId()).willReturn(PRODUCTION_REQUEST_ID);

        underTest.createProductionRequest(game, LOCATION, PROCESS_ID, RESERVED_STORAGE_ID, REQUESTED_AMOUNT);

        then(productionDispatcherProcessFactory).should().save(game, LOCATION, PROCESS_ID, PRODUCTION_REQUEST_ID);
    }
}