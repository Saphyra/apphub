package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.convoy;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.SurfaceProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy.Convoy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy.Convoys;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.Coordinates;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request.ResourceDeliveryRequest;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request.ResourceDeliveryRequests;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StorageCapacityService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.convoy_movement.ConvoyMovementProcessFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ConvoyProcessHelperTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ALLOCATION_ID = UUID.randomUUID();
    private static final UUID CONVOY_ID = UUID.randomUUID();
    private static final UUID RESOURCE_DELIVERY_REQUEST_ID = UUID.randomUUID();
    private static final Integer CAPACITY = 23;
    private static final Integer AMOUNT = 22;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final Integer LOGISTICS_WEIGHT = 32;
    private static final Integer LOGISTICS_WEIGHT_MULTIPLIER = 245;
    private static final UUID REFERRED_COORDINATE_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID CONTAINER_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID STORED_RESOURCE_ID = UUID.randomUUID();

    @Mock
    private StoredResourceConverter storedResourceConverter;

    @Mock
    private StoredResourceFactory storedResourceFactory;

    @Mock
    private ConvoyMovementProcessFactory convoyMovementProcessFactory;

    @Mock
    private GameProperties gameProperties;

    @Mock
    private StorageCapacityService storageCapacityService;

    @Mock
    private ReservedStorageConverter reservedStorageConverter;

    @InjectMocks
    private ConvoyProcessHelper underTest;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private GameData gameData;

    @Mock
    private CitizenAllocations citizenAllocations;

    @Mock
    private CitizenAllocation citizenAllocation;

    @Mock
    private Convoy convoy;

    @Mock
    private Convoys convoys;

    @Mock
    private StoredResources storedResources;

    @Mock
    private StoredResource storedResource;

    @Mock
    private StoredResourceModel storedResourceModel;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Mock
    private Game game;

    @Mock
    private Coordinates coordinates;

    @Mock
    private ReferredCoordinate referredCoordinate;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Surface surface;

    @Mock
    private Coordinate coordinate;

    @Mock
    private SurfaceProperties surfaceProperties;

    @Mock
    private ResourceDeliveryRequests resourceDeliveryRequests;

    @Mock
    private ResourceDeliveryRequest resourceDeliveryRequest;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private ReservedStorageModel reservedStorageModel;

    @Test
    void releaseCitizen() {
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(citizenAllocations.findByProcessId(PROCESS_ID)).willReturn(Optional.of(citizenAllocation));
        given(citizenAllocation.getCitizenAllocationId()).willReturn(CITIZEN_ALLOCATION_ID);

        underTest.releaseCitizen(progressDiff, gameData, PROCESS_ID);

        then(citizenAllocations).should().remove(citizenAllocation);
        then(progressDiff).should().delete(CITIZEN_ALLOCATION_ID, GameItemType.CITIZEN_ALLOCATION);
    }

    @Test
    void loadResources() {
        given(gameData.getConvoys()).willReturn(convoys);
        given(convoys.findByIdValidated(CONVOY_ID)).willReturn(convoy);
        given(convoy.getResourceDeliveryRequestId()).willReturn(RESOURCE_DELIVERY_REQUEST_ID);
        given(convoy.getCapacity()).willReturn(CAPACITY);

        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.findByAllocatedByValidated(convoy.getResourceDeliveryRequestId())).willReturn(storedResource);
        given(storedResource.getAmount()).willReturn(AMOUNT);
        given(storedResourceConverter.toModel(GAME_ID, storedResource)).willReturn(storedResourceModel);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(storedResource.getLocation()).willReturn(LOCATION);
        given(storedResource.getDataId()).willReturn(RESOURCE_DATA_ID);

        underTest.loadResources(progressDiff, gameData, CONVOY_ID);

        then(storedResource).should().decreaseAmount(AMOUNT);
        then(progressDiff).should().save(storedResourceModel);
        then(storedResourceFactory).should().save(progressDiff, gameData, LOCATION, RESOURCE_DATA_ID, AMOUNT, CONVOY_ID, ContainerType.CONVOY);
    }

    @Test
    void move_stillMoving() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(process.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);

        assertThat(underTest.move(game, LOCATION, PROCESS_ID, CONVOY_ID)).isFalse();
    }

    @Test
    void move_convoyArrived() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(process.getStatus()).willReturn(ProcessStatus.DONE);
        given(gameData.getCoordinates()).willReturn(coordinates);
        given(coordinates.getByReferenceId(CONVOY_ID)).willReturn(Collections.emptyList());

        assertThat(underTest.move(game, LOCATION, PROCESS_ID, CONVOY_ID)).isTrue();
    }

    @Test
    void move_nextWaypoint() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(process.getStatus()).willReturn(ProcessStatus.DONE);
        given(gameData.getCoordinates()).willReturn(coordinates);
        given(coordinates.getByReferenceId(CONVOY_ID)).willReturn(List.of(referredCoordinate));
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(citizenAllocations.findByProcessIdValidated(PROCESS_ID)).willReturn(citizenAllocation);
        given(citizenAllocation.getCitizenId()).willReturn(CITIZEN_ID);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.getByPlanetId(LOCATION)).willReturn(List.of(surface));
        given(referredCoordinate.getCoordinate()).willReturn(coordinate);
        given(coordinates.findByReferenceIdValidated(SURFACE_ID)).willReturn(coordinate);
        given(surface.getSurfaceType()).willReturn(SurfaceType.DESERT);
        given(gameProperties.getSurface()).willReturn(surfaceProperties);
        given(surfaceProperties.getLogisticsWeight()).willReturn(Map.of(SurfaceType.DESERT, LOGISTICS_WEIGHT));
        given(gameProperties.getLogisticsWeightMultiplier()).willReturn(LOGISTICS_WEIGHT_MULTIPLIER);
        given(referredCoordinate.getReferredCoordinateId()).willReturn(REFERRED_COORDINATE_ID);
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);
        given(game.getProgressDiff()).willReturn(progressDiff);

        assertThat(underTest.move(game, LOCATION, PROCESS_ID, CONVOY_ID)).isFalse();

        then(convoyMovementProcessFactory).should().save(game, LOCATION, PROCESS_ID, CITIZEN_ID, LOGISTICS_WEIGHT * LOGISTICS_WEIGHT_MULTIPLIER);
        then(coordinates).should().remove(referredCoordinate);
        then(game.getProgressDiff()).should().delete(REFERRED_COORDINATE_ID, GameItemType.COORDINATE);
    }

    @Test
    void unloadResources_noSpaceToUnload() {
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.findByContainerIdValidated(CONVOY_ID)).willReturn(storedResource);
        given(gameData.getConvoys()).willReturn(convoys);
        given(convoys.findByIdValidated(CONVOY_ID)).willReturn(convoy);
        given(convoy.getResourceDeliveryRequestId()).willReturn(RESOURCE_DELIVERY_REQUEST_ID);
        given(gameData.getResourceDeliveryRequests()).willReturn(resourceDeliveryRequests);
        given(resourceDeliveryRequests.findByIdValidated(RESOURCE_DELIVERY_REQUEST_ID)).willReturn(resourceDeliveryRequest);
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(resourceDeliveryRequest.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.findByIdValidated(RESERVED_STORAGE_ID)).willReturn(reservedStorage);
        given(reservedStorage.getContainerId()).willReturn(CONTAINER_ID);
        given(reservedStorage.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(storageCapacityService.getEmptyContainerCapacity(gameData, CONTAINER_ID, ContainerType.SURFACE, RESOURCE_DATA_ID)).willReturn(0);
        given(reservedStorage.getContainerType()).willReturn(ContainerType.SURFACE);
        given(storedResource.getAmount()).willReturn(AMOUNT);

        underTest.unloadResources(progressDiff, gameData, CONVOY_ID);
    }

    @Test
    void unloadResources() {
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.findByContainerIdValidated(CONVOY_ID)).willReturn(storedResource);
        given(gameData.getConvoys()).willReturn(convoys);
        given(convoys.findByIdValidated(CONVOY_ID)).willReturn(convoy);
        given(convoy.getResourceDeliveryRequestId()).willReturn(RESOURCE_DELIVERY_REQUEST_ID);
        given(gameData.getResourceDeliveryRequests()).willReturn(resourceDeliveryRequests);
        given(resourceDeliveryRequests.findByIdValidated(RESOURCE_DELIVERY_REQUEST_ID)).willReturn(resourceDeliveryRequest);
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(resourceDeliveryRequest.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.findByIdValidated(RESERVED_STORAGE_ID)).willReturn(reservedStorage);
        given(reservedStorage.getContainerId()).willReturn(CONTAINER_ID);
        given(reservedStorage.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(storageCapacityService.getEmptyContainerCapacity(gameData, CONTAINER_ID, ContainerType.SURFACE, RESOURCE_DATA_ID)).willReturn(CAPACITY);
        given(reservedStorage.getContainerType()).willReturn(ContainerType.SURFACE);
        given(storedResource.getAmount()).willReturn(AMOUNT);
        given(storedResource.getLocation()).willReturn(LOCATION);
        given(reservedStorage.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(storedResourceConverter.toModel(GAME_ID, storedResource)).willReturn(storedResourceModel);
        given(reservedStorageConverter.toModel(GAME_ID, reservedStorage)).willReturn(reservedStorageModel);
        given(storedResource.getDataId()).willReturn(RESOURCE_DATA_ID);

        assertThat(underTest.unloadResources(progressDiff, gameData, CONVOY_ID)).isTrue();

        then(storedResourceFactory).should().save(progressDiff, gameData, LOCATION, RESOURCE_DATA_ID, AMOUNT, CONTAINER_ID, ContainerType.SURFACE, EXTERNAL_REFERENCE);
        then(reservedStorage).should().decreaseAmount(AMOUNT);
        then(storedResource).should().setAmount(0);
        then(progressDiff).should().save(storedResourceModel);
        then(progressDiff).should().save(reservedStorageModel);
    }

    @Test
    void cleanup() {
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.getByContainerId(CONVOY_ID)).willReturn(List.of(storedResource));
        given(storedResource.getStoredResourceId()).willReturn(STORED_RESOURCE_ID);
        given(gameData.getConvoys()).willReturn(convoys);

        underTest.cleanup(progressDiff, gameData, CONVOY_ID);

        then(progressDiff).should().delete(STORED_RESOURCE_ID, GameItemType.STORED_RESOURCE);
        then(storedResources).should().remove(storedResource);
        then(convoys).should().remove(CONVOY_ID);
        then(progressDiff).should().delete(CONVOY_ID, GameItemType.CONVOY);
    }
}