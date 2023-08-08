package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.UseAllocatedResourceService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcessFactory;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductionOrderProcessHelperTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final String BUILDING_DATA_ID = "building-data-id";
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final Integer AMOUNT = 3124;
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID ALLOCATED_RESOURCE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private ProducerBuildingFinderService producerBuildingFinderService;

    @Mock
    private ResourceRequirementProcessFactory resourceRequirementProcessFactory;

    @Mock
    private UseAllocatedResourceService useAllocatedResourceService;

    @Mock
    private WorkProcessFactory workProcessFactory;

    @Mock
    private StoredResourceFactory storedResourceFactory;

    @Mock
    private AllocatedResourceConverter allocatedResourceConverter;

    @InjectMocks
    private ProductionOrderProcessHelper underTest;

    @Mock
    private GameData gameData;

    @Mock
    private SyncCache syncCache;

    @Mock
    private ProductionOrderProcess productionOrderProcess;

    @Mock
    private Planet planet;
    @Mock
    private Processes processes;

    @Mock
    private ProcessModel processModel;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private WorkProcess workProcess;

    @Mock
    private StoredResources storedResources;

    @Mock
    private StoredResource storedResource;

    @Mock
    private AllocatedResources allocatedResources;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private AllocatedResourceModel allocatedResourceModel;

    @Test
    void findProductionBuilding() {
        given(producerBuildingFinderService.findProducerBuildingDataId(gameData, LOCATION, RESOURCE_DATA_ID)).willReturn(Optional.of(BUILDING_DATA_ID));

        assertThat(underTest.findProductionBuilding(gameData, LOCATION, RESOURCE_DATA_ID)).isEqualTo(BUILDING_DATA_ID);
    }

    @Test
    void processResourceRequirements() {
        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(LOCATION, planet, new Planets()));
        given(planet.getOwner()).willReturn(OWNER_ID);
        given(resourceRequirementProcessFactory.createResourceRequirementProcesses(syncCache, gameData, PROCESS_ID, LOCATION, OWNER_ID, RESOURCE_DATA_ID, AMOUNT, BUILDING_DATA_ID)).willReturn(List.of(productionOrderProcess));
        given(gameData.getProcesses()).willReturn(processes);
        given(productionOrderProcess.toModel()).willReturn(processModel);

        underTest.processResourceRequirements(syncCache, gameData, PROCESS_ID, LOCATION, RESOURCE_DATA_ID, AMOUNT, BUILDING_DATA_ID);

        verify(processes).add(productionOrderProcess);
        verify(syncCache).saveGameItem(processModel);
    }

    @Test
    void startWork() {
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.findByReservedStorageIdValidated(RESERVED_STORAGE_ID)).willReturn(reservedStorage);
        given(reservedStorage.getLocation()).willReturn(LOCATION);
        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(LOCATION, planet, new Planets()));
        given(planet.getOwner()).willReturn(OWNER_ID);
        given(reservedStorage.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(reservedStorage.getAmount()).willReturn(AMOUNT);
        given(workProcessFactory.createForProduction(gameData, PROCESS_ID, LOCATION, BUILDING_DATA_ID, RESOURCE_DATA_ID, AMOUNT)).willReturn(List.of(workProcess));
        given(gameData.getProcesses()).willReturn(processes);
        given(workProcess.toModel()).willReturn(processModel);

        underTest.startWork(syncCache, gameData, PROCESS_ID, BUILDING_DATA_ID, RESERVED_STORAGE_ID);

        verify(useAllocatedResourceService).resolveAllocations(syncCache, gameData, LOCATION, OWNER_ID, PROCESS_ID);
        verify(processes).add(workProcess);
        verify(syncCache).saveGameItem(processModel);
    }

    @Test
    void storeResource_allocatedResourcePresent_storedResourceFound() {
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.findByReservedStorageIdValidated(RESERVED_STORAGE_ID)).willReturn(reservedStorage);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(reservedStorage.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(storedResources.findByLocationAndDataId(LOCATION, RESOURCE_DATA_ID)).willReturn(Optional.of(storedResource));
        given(gameData.getAllocatedResources()).willReturn(allocatedResources);
        given(allocatedResources.findByAllocatedResourceIdValidated(ALLOCATED_RESOURCE_ID)).willReturn(allocatedResource);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(allocatedResourceConverter.toModel(GAME_ID, allocatedResource)).willReturn(allocatedResourceModel);
        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(LOCATION, planet, new Planets()));
        given(planet.getOwner()).willReturn(OWNER_ID);

        underTest.storeResource(syncCache, gameData, LOCATION, RESERVED_STORAGE_ID, ALLOCATED_RESOURCE_ID, AMOUNT);

        verify(allocatedResource).increaseAmount(AMOUNT);
        verify(syncCache).saveGameItem(allocatedResourceModel);

        verify(storedResource).increaseAmount(AMOUNT);
        verify(reservedStorage).decreaseAmount(AMOUNT);
        verify(syncCache).resourceStored(OWNER_ID, LOCATION, storedResource, reservedStorage);
    }

    @Test
    void storeResource_allocatedResourceNull_storedResourceNotFound() {
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.findByReservedStorageIdValidated(RESERVED_STORAGE_ID)).willReturn(reservedStorage);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(reservedStorage.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(storedResources.findByLocationAndDataId(LOCATION, RESOURCE_DATA_ID)).willReturn(Optional.empty());
        given(storedResourceFactory.create(syncCache, gameData, LOCATION, RESOURCE_DATA_ID)).willReturn(storedResource);
        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(LOCATION, planet, new Planets()));
        given(planet.getOwner()).willReturn(OWNER_ID);

        underTest.storeResource(syncCache, gameData, LOCATION, RESERVED_STORAGE_ID, null, AMOUNT);

        verify(storedResource).increaseAmount(AMOUNT);
        verify(reservedStorage).decreaseAmount(AMOUNT);
        verify(syncCache).resourceStored(OWNER_ID, LOCATION, storedResource, reservedStorage);
    }
}