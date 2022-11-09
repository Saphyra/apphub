package com.github.saphyra.apphub.service.skyxplore.game.process.impl.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class StorageSettingProcessFactoryTest {
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
    private Game game;

    @Mock
    private Planet planet;

    @Mock
    private StorageSetting storageSetting;

    @Mock
    private Universe universe;

    @Mock
    private ProcessModel processModel;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private StorageSettings storageSettings;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);
        given(storageSetting.getStorageSettingId()).willReturn(STORAGE_SETTING_ID);

        StorageSettingProcess result = underTest.create(game, planet, storageSetting);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.getExternalReference()).isEqualTo(STORAGE_SETTING_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.STORAGE_SETTING);
    }

    @Test
    public void createFromModel() {
        given(game.getUniverse()).willReturn(universe);
        given(universe.findPlanetByIdValidated(LOCATION)).willReturn(planet);
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getStorageSettings()).willReturn(storageSettings);
        given(storageSettings.findByStorageSettingIdValidated(STORAGE_SETTING_ID)).willReturn(storageSetting);

        given(processModel.getLocation()).willReturn(LOCATION);
        given(processModel.getId()).willReturn(PROCESS_ID);
        given(processModel.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);
        given(processModel.getExternalReference()).willReturn(STORAGE_SETTING_ID);
        given(storageSetting.getStorageSettingId()).willReturn(STORAGE_SETTING_ID);

        StorageSettingProcess result = underTest.createFromModel(game, processModel);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.getExternalReference()).isEqualTo(STORAGE_SETTING_ID);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
    }
}