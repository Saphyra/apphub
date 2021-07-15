package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class StorageSettingLoaderTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer TARGET_AMOUNT = 53;
    private static final Integer PRIORITY = 463;
    private static final Integer BATCH_SIZE = 523;

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private StorageSettingLoader underTest;

    @Mock
    private StorageSettingModel storageSettingModel;

    @Test
    public void load() {
        given(gameItemLoader.loadChildren(LOCATION, GameItemType.STORAGE_SETTING, StorageSettingModel[].class)).willReturn(Arrays.asList(storageSettingModel));

        given(storageSettingModel.getId()).willReturn(STORAGE_SETTING_ID);
        given(storageSettingModel.getLocation()).willReturn(LOCATION);
        given(storageSettingModel.getLocationType()).willReturn(LocationType.PLANET.name());
        given(storageSettingModel.getDataId()).willReturn(DATA_ID);
        given(storageSettingModel.getTargetAmount()).willReturn(TARGET_AMOUNT);
        given(storageSettingModel.getPriority()).willReturn(PRIORITY);
        given(storageSettingModel.getBatchSize()).willReturn(BATCH_SIZE);

        StorageSettings result = underTest.load(LOCATION);

        assertThat(result).hasSize(1);
        StorageSetting storageSetting = result.get(0);
        assertThat(storageSetting.getStorageSettingId()).isEqualTo(STORAGE_SETTING_ID);
        assertThat(storageSetting.getLocation()).isEqualTo(LOCATION);
        assertThat(storageSetting.getLocationType()).isEqualTo(LocationType.PLANET);
        assertThat(storageSetting.getDataId()).isEqualTo(DATA_ID);
        assertThat(storageSetting.getTargetAmount()).isEqualTo(TARGET_AMOUNT);
        assertThat(storageSetting.getPriority()).isEqualTo(PRIORITY);
        assertThat(storageSetting.getBatchSize()).isEqualTo(BATCH_SIZE);
    }
}