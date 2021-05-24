package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CurrentSettingsMapperTest {
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int TARGET_AMOUNT = 123;
    private static final int BATCH_SIZE = 45;
    private static final int PRIORITY = 36;

    @InjectMocks
    private CurrentSettingsMapper underTest;

    @Test
    public void convert() {
        StorageSetting storageSetting = StorageSetting.builder()
            .storageSettingId(STORAGE_SETTING_ID)
            .dataId(DATA_ID)
            .targetAmount(TARGET_AMOUNT)
            .batchSize(BATCH_SIZE)
            .priority(PRIORITY)
            .build();

        List<StorageSettingModel> result = underTest.convert(Arrays.asList(storageSetting));

        assertThat(result).hasSize(1);
        StorageSettingModel model = result.get(0);
        assertThat(model.getStorageSettingId()).isEqualTo(STORAGE_SETTING_ID);
        assertThat(model.getDataId()).isEqualTo(DATA_ID);
        assertThat(model.getTargetAmount()).isEqualTo(TARGET_AMOUNT);
        assertThat(model.getBatchSize()).isEqualTo(BATCH_SIZE);
        assertThat(model.getPriority()).isEqualTo(PRIORITY);
    }
}