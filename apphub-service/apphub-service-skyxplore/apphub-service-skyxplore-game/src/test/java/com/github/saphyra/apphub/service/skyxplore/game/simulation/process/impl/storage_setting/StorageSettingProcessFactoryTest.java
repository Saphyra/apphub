package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
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

@ExtendWith(MockitoExtension.class)
class StorageSettingProcessFactoryTest {
    private static final int AMOUNT = 34;
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private IdGenerator idGenerator;

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

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.STORAGE_SETTING);
    }

    @Test
    void createFromModel() {
        given(game.getData()).willReturn(gameData);
        given(model.getId()).willReturn(PROCESS_ID);
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getExternalReference()).willReturn(STORAGE_SETTING_ID);
        given(model.getStatus()).willReturn(ProcessStatus.DONE);
        given(model.getData()).willReturn(Map.of(ProcessParamKeys.AMOUNT, String.valueOf(AMOUNT)));

        StorageSettingProcess result = underTest.createFromModel(game, model);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.DONE);
        assertThat(result.getExternalReference()).isEqualTo(STORAGE_SETTING_ID);
    }
}