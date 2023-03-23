package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessParamKeys;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.UseAllocatedResourceService;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class ProductionOrderProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final int AMOUNT = 100;
    private static final Integer PARENT_PRIORITY = 213;
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final String BUILDING_DATA_ID = "building-data-id";
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID ALLOCATED_RESOURCE_ID = UUID.randomUUID();
    private static final String RESOURCE_DATA_ID_STRING = "reserved-storage-id";
    private static final String ALLOCATED_RESOURCE_ID_STRING = "allocated-resource-id";

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private ProducerBuildingFinderService producerBuildingFinderService;

    @Mock
    private ResourceRequirementProcessFactory resourceRequirementProcessFactory;

    @Mock
    private UseAllocatedResourceService useAllocatedResourceService;

    @Mock
    private RequestWorkProcessFactoryForProductionOrder requestWorkProcessFactoryForProductionOrder;

    @Mock
    private StoreResourceService storeResourceService;

    @Mock
    private AllocationRemovalService allocationRemovalService;

    @Mock
    private UuidConverter uuidConverter;

    private ProductionOrderProcess underTest;

    @Mock
    private Game game;

    @Mock
    private Planet planet;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Mock
    private SyncCache syncCache;

    @Mock
    private ProductionOrderProcess productionOrderProcess;

    @Mock
    private ProcessModel processModel;

    @Mock
    private RequestWorkProcess requestWorkProcess;

    @BeforeEach
    public void setUp() {
        setUp(ProcessStatus.CREATED, null);
    }

    private void setUp(ProcessStatus status, String producerBuildingId) {
        underTest = ProductionOrderProcess.builder()
            .processId(PROCESS_ID)
            .status(status)
            .externalReference(EXTERNAL_REFERENCE)
            .gameData(game)
            .planet(planet)
            .producerBuildingDataId(producerBuildingId)
            .allocatedResource(allocatedResource)
            .reservedStorage(reservedStorage)
            .amount(AMOUNT)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Test
    public void getPriority() {
        given(game.getProcesses()).willReturn(processes);
        given(processes.findByIdValidated(EXTERNAL_REFERENCE)).willReturn(process);
        given(process.getPriority()).willReturn(PARENT_PRIORITY);

        int result = underTest.getPriority();

        assertThat(result).isEqualTo(PARENT_PRIORITY + 1);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.PRODUCTION_ORDER);
    }

    @Test
    public void work_created_noProducerBuilding() {
        given(applicationContextProxy.getBean(ProducerBuildingFinderService.class)).willReturn(producerBuildingFinderService);
        given(reservedStorage.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(producerBuildingFinderService.findProducerBuildingDataId(planet, RESOURCE_DATA_ID)).willReturn(Optional.empty());

        underTest.work(syncCache);

        assertThat(underTest.getProducerBuildingDataId()).isNull();
        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.CREATED);
        verifyNoInteractions(syncCache);
    }

    @Test
    public void work_createResourceRequirementProcesses() {
        given(applicationContextProxy.getBean(ProducerBuildingFinderService.class)).willReturn(producerBuildingFinderService);
        given(reservedStorage.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(producerBuildingFinderService.findProducerBuildingDataId(planet, RESOURCE_DATA_ID)).willReturn(Optional.of(BUILDING_DATA_ID));

        given(applicationContextProxy.getBean(ResourceRequirementProcessFactory.class)).willReturn(resourceRequirementProcessFactory);
        given(resourceRequirementProcessFactory.createResourceRequirementProcesses(syncCache, PROCESS_ID, game, planet, RESOURCE_DATA_ID, AMOUNT, BUILDING_DATA_ID)).willReturn(List.of(productionOrderProcess));
        given(game.getProcesses()).willReturn(processes);
        given(productionOrderProcess.toModel()).willReturn(processModel);

        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.PRODUCTION_ORDER)).willReturn(List.of(productionOrderProcess));
        given(productionOrderProcess.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);

        underTest.work(syncCache);

        assertThat(underTest.getProducerBuildingDataId()).isEqualTo(BUILDING_DATA_ID);
        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);

        verify(processes).add(productionOrderProcess);
        verify(syncCache).saveGameItem(processModel);
        verify(processes, times(0)).getByExternalReferenceAndType(PROCESS_ID, ProcessType.REQUEST_WORK);
    }

    @Test
    public void work_createRequestWorkProcesses() {
        setUp(ProcessStatus.IN_PROGRESS, BUILDING_DATA_ID);

        given(game.getProcesses()).willReturn(processes);
        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.PRODUCTION_ORDER)).willReturn(List.of(productionOrderProcess));
        given(productionOrderProcess.getStatus()).willReturn(ProcessStatus.DONE);

        given(applicationContextProxy.getBean(UseAllocatedResourceService.class)).willReturn(useAllocatedResourceService);

        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.REQUEST_WORK)).willReturn(Collections.emptyList());
        given(game.getGameId()).willReturn(GAME_ID);

        given(applicationContextProxy.getBean(RequestWorkProcessFactoryForProductionOrder.class)).willReturn(requestWorkProcessFactoryForProductionOrder);
        given(requestWorkProcessFactoryForProductionOrder.createWorkPointProcesses(PROCESS_ID, game, planet, BUILDING_DATA_ID, reservedStorage)).willReturn(List.of(requestWorkProcess));
        given(requestWorkProcess.toModel()).willReturn(processModel);
        given(requestWorkProcess.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);


        underTest.work(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);

        verify(useAllocatedResourceService).resolveAllocations(syncCache, GAME_ID, planet, PROCESS_ID);
        verify(processes).addAll(List.of(requestWorkProcess));
        verify(syncCache).saveGameItem(processModel);
        verify(applicationContextProxy, times(0)).getBean(StoreResourceService.class);
    }

    @Test
    public void work_finish() {
        setUp(ProcessStatus.IN_PROGRESS, BUILDING_DATA_ID);

        given(game.getProcesses()).willReturn(processes);
        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.PRODUCTION_ORDER)).willReturn(List.of(productionOrderProcess));
        given(productionOrderProcess.getStatus()).willReturn(ProcessStatus.DONE);

        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.REQUEST_WORK)).willReturn(List.of(requestWorkProcess));
        given(requestWorkProcess.getStatus()).willReturn(ProcessStatus.DONE);

        given(applicationContextProxy.getBean(StoreResourceService.class)).willReturn(storeResourceService);

        underTest.work(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.DONE);

        verify(storeResourceService).storeResource(syncCache, game, planet, reservedStorage, allocatedResource, AMOUNT);
    }

    @Test
    public void cancel() {
        given(applicationContextProxy.getBean(AllocationRemovalService.class)).willReturn(allocationRemovalService);

        given(game.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(productionOrderProcess));

        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);

        underTest.cancel(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);

        verify(allocationRemovalService).removeAllocationsAndReservations(syncCache, planet, PROCESS_ID);
        verify(productionOrderProcess).cleanup(syncCache);
        verify(syncCache).saveGameItem(any(ProcessModel.class));
    }

    @Test
    public void cleanup() {
        given(applicationContextProxy.getBean(AllocationRemovalService.class)).willReturn(allocationRemovalService);

        given(game.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(productionOrderProcess));

        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);

        underTest.cleanup(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);

        verify(allocationRemovalService).removeAllocationsAndReservations(syncCache, planet, PROCESS_ID);
        verify(productionOrderProcess).cleanup(syncCache);
        verify(syncCache).saveGameItem(any(ProcessModel.class));
    }

    @Test
    public void toModel() {
        setUp(ProcessStatus.IN_PROGRESS, BUILDING_DATA_ID);

        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);

        given(game.getGameId()).willReturn(GAME_ID);
        given(planet.getPlanetId()).willReturn(PLANET_ID);

        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);
        given(uuidConverter.convertDomain(RESERVED_STORAGE_ID)).willReturn(RESOURCE_DATA_ID_STRING);
        given(uuidConverter.convertDomain(eq(allocatedResource), any())).willReturn(ALLOCATED_RESOURCE_ID_STRING);

        ProcessModel result = underTest.toModel();

        assertThat(result.getId()).isEqualTo(PROCESS_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.PROCESS);
        assertThat(result.getProcessType()).isEqualTo(ProcessType.PRODUCTION_ORDER);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
        assertThat(result.getLocation()).isEqualTo(PLANET_ID);
        assertThat(result.getLocationType()).isEqualTo(LocationType.PLANET.name());
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getData()).containsEntry(ProcessParamKeys.PRODUCER_BUILDING_DATA_ID, BUILDING_DATA_ID);
        assertThat(result.getData()).containsEntry(ProcessParamKeys.RESERVED_STORAGE_ID, RESOURCE_DATA_ID_STRING);
        assertThat(result.getData()).containsEntry(ProcessParamKeys.ALLOCATED_RESOURCE_ID, ALLOCATED_RESOURCE_ID_STRING);
        assertThat(result.getData()).containsEntry(ProcessParamKeys.AMOUNT, String.valueOf(AMOUNT));
    }
}