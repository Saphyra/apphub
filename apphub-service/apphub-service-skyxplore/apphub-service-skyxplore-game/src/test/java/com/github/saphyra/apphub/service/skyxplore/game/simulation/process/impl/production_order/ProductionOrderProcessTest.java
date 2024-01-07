package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductionOrderProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID ALLOCATED_RESOURCE_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final int AMOUNT = 3412;
    private static final Integer PRIORITY = 342;
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final String PRODUCER_DATA_ID = "producer-data-id";
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private GameData gameData;

    @Mock
    private ProductionOrderProcessConditions conditions;

    @Mock
    private ProductionOrderProcessHelper helper;

    @Mock
    private AllocationRemovalService allocationRemovalService;

    private ProductionOrderProcess underTest;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Mock
    private SyncCache syncCache;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private ReservedStorage reservedStorage;

    @BeforeEach
    void setUp() {
        underTest = ProductionOrderProcess.builder()
            .processId(PROCESS_ID)
            .status(ProcessStatus.CREATED)
            .allocatedResourceId(ALLOCATED_RESOURCE_ID)
            .reservedStorageId(RESERVED_STORAGE_ID)
            .externalReference(EXTERNAL_REFERENCE)
            .gameData(gameData)
            .location(LOCATION)
            .amount(AMOUNT)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Test
    void getPriority() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.findByIdValidated(EXTERNAL_REFERENCE)).willReturn(process);
        given(process.getPriority()).willReturn(PRIORITY);

        assertThat(underTest.getPriority()).isEqualTo(PRIORITY + 1);
    }

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.PRODUCTION_ORDER);
    }

    @Test
    void work_productionBuildingNotAvailable() {
        given(applicationContextProxy.getBean(ProductionOrderProcessHelper.class)).willReturn(helper);

        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.findByReservedStorageIdValidated(RESERVED_STORAGE_ID)).willReturn(reservedStorage);
        given(reservedStorage.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(helper.findProductionBuilding(gameData, LOCATION, RESOURCE_DATA_ID)).willReturn(null);

        underTest.work(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.CREATED);
    }

    @Test
    void work_selectProducerBuildingAndProcessResourceRequirements() {
        given(applicationContextProxy.getBean(ProductionOrderProcessHelper.class)).willReturn(helper);
        given(applicationContextProxy.getBean(ProductionOrderProcessConditions.class)).willReturn(conditions);

        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.findByReservedStorageIdValidated(RESERVED_STORAGE_ID)).willReturn(reservedStorage);
        given(reservedStorage.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(helper.findProductionBuilding(gameData, LOCATION, RESOURCE_DATA_ID)).willReturn(PRODUCER_DATA_ID);
        given(conditions.requiredResourcesPresent(gameData, PROCESS_ID)).willReturn(false);

        underTest.work(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);

        verify(helper).processResourceRequirements(syncCache, gameData, PROCESS_ID, LOCATION, RESOURCE_DATA_ID, AMOUNT, PRODUCER_DATA_ID);
        verify(conditions, times(0)).workStarted(gameData, PROCESS_ID);
    }

    @Test
    void work_startWork() {
        given(applicationContextProxy.getBean(ProductionOrderProcessHelper.class)).willReturn(helper);
        given(applicationContextProxy.getBean(ProductionOrderProcessConditions.class)).willReturn(conditions);

        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.findByReservedStorageIdValidated(RESERVED_STORAGE_ID)).willReturn(reservedStorage);
        given(reservedStorage.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(helper.findProductionBuilding(gameData, LOCATION, RESOURCE_DATA_ID)).willReturn(PRODUCER_DATA_ID);
        given(conditions.requiredResourcesPresent(gameData, PROCESS_ID)).willReturn(true);
        given(conditions.workStarted(gameData, PROCESS_ID)).willReturn(false);
        given(conditions.workDone(gameData, PROCESS_ID)).willReturn(false);

        underTest.work(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);

        verify(helper).processResourceRequirements(syncCache, gameData, PROCESS_ID, LOCATION, RESOURCE_DATA_ID, AMOUNT, PRODUCER_DATA_ID);
        verify(helper).startWork(syncCache, gameData, PROCESS_ID, PRODUCER_DATA_ID, RESERVED_STORAGE_ID);
        verify(helper, times(0)).storeResource(syncCache, gameData, LOCATION, RESERVED_STORAGE_ID, ALLOCATED_RESOURCE_ID, AMOUNT);
    }

    @Test
    void work_storeResources() {
        given(applicationContextProxy.getBean(ProductionOrderProcessHelper.class)).willReturn(helper);
        given(applicationContextProxy.getBean(ProductionOrderProcessConditions.class)).willReturn(conditions);

        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.findByReservedStorageIdValidated(RESERVED_STORAGE_ID)).willReturn(reservedStorage);
        given(reservedStorage.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(helper.findProductionBuilding(gameData, LOCATION, RESOURCE_DATA_ID)).willReturn(PRODUCER_DATA_ID);
        given(conditions.requiredResourcesPresent(gameData, PROCESS_ID)).willReturn(true);
        given(conditions.workStarted(gameData, PROCESS_ID)).willReturn(false);
        given(conditions.workDone(gameData, PROCESS_ID)).willReturn(true);

        underTest.work(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.DONE);

        verify(helper).processResourceRequirements(syncCache, gameData, PROCESS_ID, LOCATION, RESOURCE_DATA_ID, AMOUNT, PRODUCER_DATA_ID);
        verify(helper).startWork(syncCache, gameData, PROCESS_ID, PRODUCER_DATA_ID, RESERVED_STORAGE_ID);
        verify(helper).storeResource(syncCache, gameData, LOCATION, RESERVED_STORAGE_ID, ALLOCATED_RESOURCE_ID, AMOUNT);
    }

    @Test
    void cleanup() {
        given(applicationContextProxy.getBean(AllocationRemovalService.class)).willReturn(allocationRemovalService);
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(new UuidConverter());

        underTest.cleanup(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);

        verify(allocationRemovalService).removeAllocationsAndReservations(syncCache, gameData, PROCESS_ID);
        verify(process).cleanup(syncCache);
        verify(syncCache).saveGameItem(underTest.toModel());
    }

    @Test
    void toModel() {
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(new UuidConverter());
        given(gameData.getGameId()).willReturn(GAME_ID);

        ProcessModel result = underTest.toModel();

        assertThat(result.getId()).isEqualTo(PROCESS_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.PROCESS);
        assertThat(result.getProcessType()).isEqualTo(ProcessType.PRODUCTION_ORDER);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getData()).containsEntry(ProcessParamKeys.PRODUCER_BUILDING_DATA_ID, null);
        assertThat(result.getData()).containsEntry(ProcessParamKeys.RESERVED_STORAGE_ID, RESERVED_STORAGE_ID.toString());
        assertThat(result.getData()).containsEntry(ProcessParamKeys.ALLOCATED_RESOURCE_ID, ALLOCATED_RESOURCE_ID.toString());
        assertThat(result.getData()).containsEntry(ProcessParamKeys.AMOUNT, String.valueOf(AMOUNT));
    }
}