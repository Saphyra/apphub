package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_delivery;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizens;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy.Convoy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy.ConvoyFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy.Convoys;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request.ResourceDeliveryRequest;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request.ResourceDeliveryRequests;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.convoy.ConvoyProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.convoy.ConvoyProcessFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ResourceDeliveryProcessHelperTest {
    private static final UUID RESOURCE_DELIVERY_REQUEST_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final Integer DELIVERY_NEEDED = 1000;
    private static final Integer CONVOY_CAPACITY = 300;
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID CONVOY_ID = UUID.randomUUID();

    @Mock
    private ConvoyFactory convoyFactory;

    @Mock
    private CitizenAllocationFactory citizenAllocationFactory;

    @Mock
    private ConvoyProcessFactory convoyProcessFactory;

    @InjectMocks
    private ResourceDeliveryProcessHelper underTest;

    @Mock
    private GameData gameData;

    @Mock
    private ResourceDeliveryRequests resourceDeliveryRequests;

    @Mock
    private ResourceDeliveryRequest resourceDeliveryRequest;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private Convoys convoys;

    @Mock
    private Convoy convoy;

    @Mock
    private Citizens citizens;

    @Mock
    private Citizen citizen;

    @Mock
    private CitizenAllocations citizenAllocations;

    @Mock
    private CitizenAllocation citizenAllocation;

    @Mock
    private Game game;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private ConvoyProcess convoyProcess;

    @Test
    void calculateToDeliver() {
        given(gameData.getResourceDeliveryRequests()).willReturn(resourceDeliveryRequests);
        given(resourceDeliveryRequests.findByIdValidated(RESOURCE_DELIVERY_REQUEST_ID)).willReturn(resourceDeliveryRequest);
        given(resourceDeliveryRequest.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.findByIdValidated(RESERVED_STORAGE_ID)).willReturn(reservedStorage);
        given(reservedStorage.getAmount()).willReturn(DELIVERY_NEEDED);
        given(gameData.getConvoys()).willReturn(convoys);
        given(convoys.getByResourceDeliveryRequestId(RESOURCE_DELIVERY_REQUEST_ID)).willReturn(List.of(convoy));
        given(convoy.getCapacity()).willReturn(CONVOY_CAPACITY);

        assertThat(underTest.calculateToDeliver(gameData, RESOURCE_DELIVERY_REQUEST_ID)).isEqualTo(DELIVERY_NEEDED - CONVOY_CAPACITY);
    }

    @Test
    void assembleConvoy_citizenNotAvailable() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getCitizens()).willReturn(citizens);
        given(citizens.getByLocation(LOCATION)).willReturn(List.of(citizen));
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(citizenAllocations.stream()).willReturn(Stream.of(citizenAllocation));
        given(citizenAllocation.getCitizenId()).willReturn(CITIZEN_ID);
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);

        assertThat(underTest.assembleConvoy(game, LOCATION, PROCESS_ID, RESOURCE_DELIVERY_REQUEST_ID, DELIVERY_NEEDED)).isEmpty();
    }

    @Test
    void assembleConvoy() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getCitizens()).willReturn(citizens);
        given(citizens.getByLocation(LOCATION)).willReturn(List.of(citizen));
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(citizenAllocations.stream()).willReturn(Stream.of(citizenAllocation));
        given(citizenAllocation.getCitizenId()).willReturn(UUID.randomUUID());
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(convoyFactory.save(progressDiff, gameData, LOCATION, RESOURCE_DELIVERY_REQUEST_ID, DELIVERY_NEEDED)).willReturn(convoy);
        given(convoy.getConvoyId()).willReturn(CONVOY_ID);
        given(convoy.getCapacity()).willReturn(CONVOY_CAPACITY);
        given(convoyProcessFactory.save(game, LOCATION, PROCESS_ID, CONVOY_ID)).willReturn(convoyProcess);
        given(convoyProcess.getProcessId()).willReturn(PROCESS_ID);

        assertThat(underTest.assembleConvoy(game, LOCATION, PROCESS_ID, RESOURCE_DELIVERY_REQUEST_ID, DELIVERY_NEEDED)).contains(CONVOY_CAPACITY);

        then(citizenAllocationFactory).should().save(progressDiff, gameData, CITIZEN_ID, PROCESS_ID);
    }
}