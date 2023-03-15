package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class StorageSettingFactoryTest {
    private static final int TARGET_AMOUNT = 2343;
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer PRIORITY = 26357;
    private static final Integer BATCH_SIZE = 5875;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private StorageSettingFactory underTest;

    @Test
    public void create() {
        StorageSettingApiModel request = StorageSettingApiModel.builder()
            .dataId(DATA_ID)
            .priority(PRIORITY)
            .batchSize(BATCH_SIZE)
            .targetAmount(TARGET_AMOUNT)
            .build();

        given(idGenerator.randomUuid()).willReturn(STORAGE_SETTING_ID);

        StorageSetting result = underTest.create(request, LOCATION, LocationType.PLANET);

        assertThat(result.getStorageSettingId()).isEqualTo(STORAGE_SETTING_ID);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getLocationType()).isEqualTo(LocationType.PLANET);
        assertThat(result.getTargetAmount()).isEqualTo(TARGET_AMOUNT);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
        assertThat(result.getBatchSize()).isEqualTo(BATCH_SIZE);
    }
}