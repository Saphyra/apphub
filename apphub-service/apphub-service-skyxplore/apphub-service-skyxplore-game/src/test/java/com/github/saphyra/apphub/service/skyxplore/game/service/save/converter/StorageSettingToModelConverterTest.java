package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class StorageSettingToModelConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int TARGET_AMOUNT = 254;
    private static final int PRIORITY = 4527;
    private static final int BATCH_SIZE = 132;

    @InjectMocks
    private StorageSettingToModelConverter underTest;

    @Mock
    private Game game;

    @Test
    public void convert() {
        given(game.getGameId()).willReturn(GAME_ID);

        StorageSetting storageSetting = StorageSetting.builder()
            .storageSettingId(STORAGE_SETTING_ID)
            .location(LOCATION)
            .dataId(DATA_ID)
            .targetAmount(TARGET_AMOUNT)
            .priority(PRIORITY)
            .batchSize(BATCH_SIZE)
            .build();

        List<StorageSettingModel> result = underTest.convert(GAME_ID, Arrays.asList(storageSetting));

        assertThat(result.get(0).getId()).isEqualTo(STORAGE_SETTING_ID);
        assertThat(result.get(0).getGameId()).isEqualTo(GAME_ID);
        assertThat(result.get(0).getType()).isEqualTo(GameItemType.STORAGE_SETTING);
        assertThat(result.get(0).getLocation()).isEqualTo(LOCATION);
        assertThat(result.get(0).getDataId()).isEqualTo(DATA_ID);
        assertThat(result.get(0).getTargetAmount()).isEqualTo(TARGET_AMOUNT);
        assertThat(result.get(0).getPriority()).isEqualTo(PRIORITY);
        assertThat(result.get(0).getBatchSize()).isEqualTo(BATCH_SIZE);
    }
}