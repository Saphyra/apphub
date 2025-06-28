package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StorageSettingProcessFactoryTest {
    private static final int AMOUNT = 34;
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String RESERVED_STORAGE_ID_STRING = "reserved-storage-id";
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID CONTAINER_ID = UUID.randomUUID();
    private static final String RESOURCE_DATA_ID = "resource-data-id";

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ReservedStorageFactory reservedStorageFactory;

    @InjectMocks
    private StorageSettingProcessFactory underTest;

    @Mock
    private GameData gameData;

    @Mock
    private StorageSetting storageSetting;

    @Mock
    private Game game;

    @Mock
    private ProcessModel model;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private Processes processes;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.STORAGE_SETTING);
    }

    @Test
    void createFromModel() {
        given(model.getId()).willReturn(PROCESS_ID);
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getExternalReference()).willReturn(STORAGE_SETTING_ID);
        given(model.getStatus()).willReturn(ProcessStatus.DONE);
        given(model.getData()).willReturn(Map.of(
            ProcessParamKeys.RESERVED_STORAGE_ID, RESERVED_STORAGE_ID_STRING,
            ProcessParamKeys.AMOUNT, String.valueOf(AMOUNT)
        ));
        given(uuidConverter.convertEntity(RESERVED_STORAGE_ID_STRING)).willReturn(RESERVED_STORAGE_ID);

        StorageSettingProcess result = underTest.createFromModel(game, model);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.DONE);
        assertThat(result.getExternalReference()).isEqualTo(STORAGE_SETTING_ID);
        assertThat(result.getType()).isEqualTo(ProcessType.STORAGE_SETTING);
    }

    @Test
    void save() {
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(game.getData()).willReturn(gameData);
        given(storageSetting.getStorageSettingId()).willReturn(STORAGE_SETTING_ID);
        given(storageSetting.getLocation()).willReturn(LOCATION);
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);
        given(reservedStorageFactory.save(progressDiff, gameData, CONTAINER_ID, ContainerType.STORAGE, PROCESS_ID, RESOURCE_DATA_ID, AMOUNT)).willReturn(reservedStorage);
        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);
        given(gameData.getProcesses()).willReturn(processes);
        given(storageSetting.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);

        StorageSettingProcess result = underTest.save(game, storageSetting, CONTAINER_ID, AMOUNT);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
        assertThat(result.getExternalReference()).isEqualTo(STORAGE_SETTING_ID);
        assertThat(result.getType()).isEqualTo(ProcessType.STORAGE_SETTING);

        then(processes).should().add(result);
        then(progressDiff).should().save(result.toModel());
    }
}