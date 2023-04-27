package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priorities;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priority;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSettings;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StorageSettingProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();
    private static final Integer AMOUNT = 234;
    private static final Integer PLANET_PRIORITY = 342;
    private static final Integer STORAGE_SETTING_PRIORITY = 34;
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private GameData gameData;

    @Mock
    private SyncCache syncCache;

    @Mock
    private StorageSettingProcessConditions conditions;

    @Mock
    private StorageSettingProcessHelper helper;

    @Mock
    private AllocationRemovalService allocationRemovalService;

    private StorageSettingProcess underTest;

    @Mock
    private Priorities priorities;

    @Mock
    private Priority priority;

    @Mock
    private StorageSettings storageSettings;

    @Mock
    private StorageSetting storageSetting;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Mock
    private Planet planet;

    @BeforeEach
    void setUp() {
        underTest = StorageSettingProcess.builder()
            .processId(PROCESS_ID)
            .status(ProcessStatus.CREATED)
            .location(LOCATION)
            .gameData(gameData)
            .storageSettingId(STORAGE_SETTING_ID)
            .amount(AMOUNT)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Test
    void getExternalReference() {
        assertThat(underTest.getExternalReference()).isEqualTo(STORAGE_SETTING_ID);
    }

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.STORAGE_SETTING);
    }

    @Test
    void getPriority() {
        given(gameData.getPriorities()).willReturn(priorities);
        given(priorities.findByLocationAndType(LOCATION, PriorityType.INDUSTRY)).willReturn(priority);
        given(priority.getValue()).willReturn(PLANET_PRIORITY);
        given(gameData.getStorageSettings()).willReturn(storageSettings);
        given(storageSettings.findByStorageSettingIdValidated(STORAGE_SETTING_ID)).willReturn(storageSetting);
        given(storageSetting.getPriority()).willReturn(STORAGE_SETTING_PRIORITY);

        assertThat(underTest.getPriority()).isEqualTo(PLANET_PRIORITY * STORAGE_SETTING_PRIORITY * GameConstants.PROCESS_PRIORITY_MULTIPLIER);
    }

    @Test
    void work() {
        given(applicationContextProxy.getBean(StorageSettingProcessConditions.class)).willReturn(conditions);
        given(applicationContextProxy.getBean(StorageSettingProcessHelper.class)).willReturn(helper);
        given(gameData.getStorageSettings()).willReturn(storageSettings);
        given(storageSettings.findByStorageSettingIdValidated(STORAGE_SETTING_ID)).willReturn(storageSetting);
        given(conditions.isFinished(gameData, PROCESS_ID))
            .willReturn(false)
            .willReturn(true);

        underTest.work(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);

        underTest.work(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);

        verify(helper).orderResources(syncCache, gameData, PROCESS_ID, storageSetting, AMOUNT);
    }

    @Test
    void cleanup() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(LOCATION, planet, new Planets()));
        given(planet.getOwner()).willReturn(OWNER_ID);
        given(applicationContextProxy.getBean(AllocationRemovalService.class)).willReturn(allocationRemovalService);

        underTest.cleanup(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);

        verify(process).cleanup(syncCache);
        verify(allocationRemovalService).removeAllocationsAndReservations(syncCache, gameData, LOCATION, OWNER_ID, PROCESS_ID);
        verify(syncCache).storageModified(OWNER_ID, LOCATION);
        verify(syncCache).saveGameItem(underTest.toModel());
    }

    @Test
    void toModel() {
        given(gameData.getGameId()).willReturn(GAME_ID);

        ProcessModel result = underTest.toModel();

        assertThat(result.getId()).isEqualTo(PROCESS_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.PROCESS);
        assertThat(result.getProcessType()).isEqualTo(ProcessType.STORAGE_SETTING);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getExternalReference()).isEqualTo(STORAGE_SETTING_ID);
        assertThat(result.getData()).containsEntry(ProcessParamKeys.AMOUNT, String.valueOf(AMOUNT));
    }
}