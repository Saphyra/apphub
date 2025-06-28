package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priorities;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priority;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StorageSettingProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();
    private static final Integer AMOUNT = 234;
    private static final Integer PLANET_PRIORITY = 342;
    private static final Integer STORAGE_SETTING_PRIORITY = 34;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final String RESERVED_STORAGE_ID_STRING = "reserved-storage-id";

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private GameData gameData;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private StorageSettingProcessHelper helper;

    @Mock
    private AllocationRemovalService allocationRemovalService;

    @Mock
    private Game game;

    @Mock
    private UuidConverter uuidConverter;

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

    @BeforeEach
    void setUp() {
        underTest = StorageSettingProcess.builder()
            .processId(PROCESS_ID)
            .status(ProcessStatus.CREATED)
            .location(LOCATION)
            .storageSettingId(STORAGE_SETTING_ID)
            .reservedStorageId(RESERVED_STORAGE_ID)
            .amount(AMOUNT)
            .applicationContextProxy(applicationContextProxy)
            .game(game)
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
        given(game.getData()).willReturn(gameData);
        given(gameData.getPriorities()).willReturn(priorities);
        given(priorities.findByLocationAndType(LOCATION, PriorityType.INDUSTRY)).willReturn(priority);
        given(priority.getValue()).willReturn(PLANET_PRIORITY);
        given(gameData.getStorageSettings()).willReturn(storageSettings);
        given(storageSettings.findByStorageSettingIdValidated(STORAGE_SETTING_ID)).willReturn(storageSetting);
        given(storageSetting.getPriority()).willReturn(STORAGE_SETTING_PRIORITY);

        assertThat(underTest.getPriority()).isEqualTo(PLANET_PRIORITY * STORAGE_SETTING_PRIORITY * GameConstants.PROCESS_PRIORITY_MULTIPLIER);
    }

    @Test
    void work_notFinished() {
        given(applicationContextProxy.getBean(StorageSettingProcessHelper.class)).willReturn(helper);
        given(game.getData()).willReturn(gameData);
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(process.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);

        then(helper).should().orderResources(game, LOCATION, PROCESS_ID, RESERVED_STORAGE_ID);
    }

    @Test
    void work_finished() {
        given(applicationContextProxy.getBean(StorageSettingProcessHelper.class)).willReturn(helper);
        given(game.getData()).willReturn(gameData);
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(process.getStatus()).willReturn(ProcessStatus.DONE);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);

        then(helper).should().orderResources(game, LOCATION, PROCESS_ID, RESERVED_STORAGE_ID);
    }

    @Test
    void cleanup() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(applicationContextProxy.getBean(AllocationRemovalService.class)).willReturn(allocationRemovalService);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.cleanup();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);

        verify(process).cleanup();
        verify(allocationRemovalService).removeAllocationsAndReservations(progressDiff, gameData, PROCESS_ID);
    }

    @Test
    void toModel() {
        given(game.getGameId()).willReturn(GAME_ID);
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);
        given(uuidConverter.convertDomain(RESERVED_STORAGE_ID)).willReturn(RESERVED_STORAGE_ID_STRING);

        ProcessModel result = underTest.toModel();

        assertThat(result.getId()).isEqualTo(PROCESS_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.PROCESS);
        assertThat(result.getProcessType()).isEqualTo(ProcessType.STORAGE_SETTING);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getExternalReference()).isEqualTo(STORAGE_SETTING_ID);
        assertThat(result.getData()).containsEntry(ProcessParamKeys.AMOUNT, String.valueOf(AMOUNT));
        assertThat(result.getData()).containsEntry(ProcessParamKeys.RESERVED_STORAGE_ID, RESERVED_STORAGE_ID_STRING);
    }
}