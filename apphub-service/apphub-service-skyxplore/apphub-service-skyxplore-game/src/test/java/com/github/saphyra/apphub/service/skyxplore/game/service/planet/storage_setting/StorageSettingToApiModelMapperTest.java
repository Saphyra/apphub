package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class StorageSettingToApiModelMapperTest {
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int TARGET_AMOUNT = 123;
    private static final int PRIORITY = 36;

    @InjectMocks
    private StorageSettingToApiModelMapper underTest;

    @Test
    public void convert() {
        StorageSetting storageSetting = StorageSetting.builder()
            .storageSettingId(STORAGE_SETTING_ID)
            .dataId(DATA_ID)
            .targetAmount(TARGET_AMOUNT)
            .priority(PRIORITY)
            .build();

        List<StorageSettingApiModel> result = underTest.convert(Arrays.asList(storageSetting));

        assertThat(result).hasSize(1);
        StorageSettingApiModel model = result.get(0);
        assertThat(model.getStorageSettingId()).isEqualTo(STORAGE_SETTING_ID);
        assertThat(model.getDataId()).isEqualTo(DATA_ID);
        assertThat(model.getTargetAmount()).isEqualTo(TARGET_AMOUNT);
        assertThat(model.getPriority()).isEqualTo(PRIORITY);
    }
}